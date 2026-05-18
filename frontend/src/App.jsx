import { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [products, setProducts] = useState([]);
  const [cartCount, setCartCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Zaciąganie danych z backendu natychmiast po załadowaniu (zamontowaniu) komponentu
  useEffect(() => {
    fetch('http://localhost:8080/api/products')
      .then(response => {
        if (!response.ok) {
          throw new Error('Odpowiedź serwera: niezgodny status HTTP');
        }
        return response.json();
      })
      .then(data => {
        setProducts(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Błąd pobierania produktów:', err);
        setError('Brak połączenia! Upewnij się, że backend Java działa (na porcie 8080).');
        setLoading(false);
      });
  }, []);

  const handleAddToCart = () => {
    setCartCount(prev => prev + 1);
  };

  return (
    <div className="app">
      <header className="header">
        <div className="brand">NexusCart</div>
        <div className="header-actions">
          <button className="cart-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            Cart
            {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
          </button>
        </div>
      </header>

      <main className="main-content">
        <h1 className="page-title">Featured Products</h1>

        {loading && <p style={{ color: 'var(--text-secondary)' }}>Ładowanie produktów z backendu...</p>}
        {error && <p style={{ color: 'var(--danger)' }}>{error}</p>}

        {!loading && !error && products.length === 0 && (
          <p style={{ color: 'var(--text-secondary)' }}>Brak produktów w bazie danych. Dodaj je np. przez Swaggera bądź plik data.sql!</p>
        )}

        <div className="products-grid">
          {products.map(product => (
            <div key={product.id} className="product-card">
              <div className="image-placeholder">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <circle cx="8.5" cy="8.5" r="1.5"></circle>
                  <polyline points="21 15 16 10 5 21"></polyline>
                </svg>
              </div>
              <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                <p className="product-desc">{product.description}</p>
              </div>
              <div className="product-footer">
                <span className="price">${product.price?.toFixed(2) || '0.00'}</span>
                <button className="add-btn" onClick={handleAddToCart}>
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

export default App;
