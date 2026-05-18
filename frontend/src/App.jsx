import { useState, useEffect } from 'react';
import './App.css';

const API_BASE = 'http://localhost:8080/api';
const USER_ID = 1; // Symulacja zalogowanego użytkownika

function App() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState({ items: [], totalCartPrice: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);

  // Zaciąganie danych o produktach i koszyku
  useEffect(() => {
    fetchProducts();
    fetchCart();
  }, []);

  const fetchProducts = () => {
    fetch(`${API_BASE}/products`)
      .then(res => res.json())
      .then(data => {
        setProducts(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Błąd pobierania produktów:', err);
        setError('Błąd połączenia z backendem (port 8080).');
        setLoading(false);
      });
  };

  const fetchCart = () => {
    fetch(`${API_BASE}/carts`, {
      headers: { 'X-User-Id': USER_ID }
    })
      .then(res => res.json())
      .then(data => setCart(data))
      .catch(err => console.error('Błąd pobierania koszyka:', err));
  };

  const addToCart = (productId) => {
    fetch(`${API_BASE}/carts/items`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': USER_ID
      },
      body: JSON.stringify({ productId, quantity: 1 })
    })
      .then(res => res.json())
      .then(data => {
        setCart(data);
        // Opcjonalnie: otwórz koszyk po dodaniu
        // setIsCartOpen(true);
      })
      .catch(err => alert('Błąd: Nie udało się dodać do koszyka (sprawdź dostępność).'));
  };

  const removeFromCart = (itemId) => {
    fetch(`${API_BASE}/carts/items/${itemId}`, {
      method: 'DELETE',
      headers: { 'X-User-Id': USER_ID }
    })
      .then(res => res.json())
      .then(data => setCart(data))
      .catch(err => console.error('Błąd usuwania:', err));
  };

  const placeOrder = () => {
    fetch(`${API_BASE}/orders`, {
      method: 'POST',
      headers: { 'X-User-Id': USER_ID }
    })
      .then(res => {
        if (res.ok) {
          setOrderSuccess(true);
          setCart({ items: [], totalCartPrice: 0 });
          setTimeout(() => setOrderSuccess(false), 5000);
        } else {
          alert('Błąd składania zamówienia.');
        }
      })
      .catch(err => console.error('Błąd zamówienia:', err));
  };

  return (
    <div className="app">
      <header className="header">
        <div className="brand">NexusCart</div>
        <div className="header-actions">
          <button className="cart-btn" onClick={() => setIsCartOpen(true)}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            Cart
            {cart.items?.length > 0 && (
              <span className="cart-badge">
                {cart.items.reduce((acc, item) => acc + item.quantity, 0)}
              </span>
            )}
          </button>
        </div>
      </header>

      <main className="main-content">
        <h1 className="page-title">Featured Products</h1>

        {loading && <p style={{ color: 'var(--text-secondary)' }}>Ładowanie...</p>}
        {error && <p style={{ color: 'var(--danger)' }}>{error}</p>}

        <div className="products-grid">
          {products.map(product => (
            <div key={product.id} className="product-card">
              <div className="product-image-container">
                {product.imageUrl ? (
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className="product-image"
                    onError={(e) => {
                      e.target.onerror = null;
                      e.target.src = 'https://placehold.co/400x400/1e293b/white?text=No+Image';
                    }}
                  />
                ) : (
                  <div className="image-placeholder">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                      <circle cx="8.5" cy="8.5" r="1.5"></circle>
                      <polyline points="21 15 16 10 5 21"></polyline>
                    </svg>
                  </div>
                )}
              </div>
              <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                <p className="product-desc">{product.description}</p>
              </div>
              <div className="product-footer">
                <span className="price">${product.price?.toFixed(2)}</span>
                <button className="add-btn" onClick={() => addToCart(product.id)}>
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>

      {/* Cart Modal Overlay */}
      {isCartOpen && (
        <div className="cart-overlay" onClick={() => setIsCartOpen(false)}>
          <div className="cart-modal" onClick={e => e.stopPropagation()}>
            <div className="cart-header">
              <h2>Your Cart</h2>
              <button className="close-btn" onClick={() => setIsCartOpen(false)}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
            </div>

            <div className="cart-items">
              {cart.items?.length === 0 ? (
                <p style={{ color: 'var(--text-secondary)', textAlign: 'center' }}>Your cart is empty.</p>
              ) : (
                cart.items.map(item => (
                  <div key={item.id} className="cart-item">
                    <div className="cart-item-img">
                      <img src={item.imageUrl} alt={item.productName} />
                    </div>
                    <div className="cart-item-info">
                      <div className="cart-item-name">{item.productName}</div>
                      <div className="cart-item-details">
                        <span className="cart-item-price">
                          {item.quantity} x ${item.unitPrice.toFixed(2)}
                        </span>
                        <button className="remove-item-btn" onClick={() => removeFromCart(item.id)}>
                          Remove
                        </button>
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="cart-footer">
              {orderSuccess && (
                <div className="success-message">
                  Order placed successfully! 🎉
                </div>
              )}
              <div className="cart-total">
                <span>Total:</span>
                <span>${cart.totalCartPrice?.toFixed(2) || '0.00'}</span>
              </div>
              <button
                className="order-btn"
                disabled={cart.items?.length === 0}
                onClick={placeOrder}
              >
                Place Order
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
