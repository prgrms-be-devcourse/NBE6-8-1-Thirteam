'use client';

import { useState } from 'react';
import Modal from './Modal';
import Checkout from './Checkout';

export interface MenuItem {
  name: string;
  price: number;
  image: string;
  category: string;
  quantity: number;
}

interface CartProps {
  cartItems: MenuItem[];
  setCartItems: (items: MenuItem[]) => void;
}

export default function Cart({ cartItems, setCartItems }: CartProps) {
  const [showModal, setShowModal] = useState(false);

  const increment = (name: string) => {
    setCartItems(
      cartItems.map((item) =>
        item.name === name ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  };

  const decrement = (name: string) => {
    const updated = cartItems
      .map((item) =>
        item.name === name ? { ...item, quantity: item.quantity - 1 } : item
      )
      .filter((item) => item.quantity > 0);
    setCartItems(updated);
  };

  const removeItem = (name: string) => {
    setCartItems(cartItems.filter((item) => item.name !== name));
  };

  const clearCart = () => {
    setCartItems([]);
  };

  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleCheckout = () => {
    if (total === 0) {
      alert('🛍 상품을 하나 이상 담아주세요!');
      return;
    }
    setShowModal(true);
  };

  return (
    <div className="bg-white p-4 rounded-lg shadow-lg w-[360px] text-base">
      <h2 className="text-xl font-bold mb-4">🛒 장바구니</h2>
      <ul className="space-y-2 max-h-60 overflow-y-auto">
        {cartItems.map((item) => (
          <li key={item.name} className="flex justify-between items-center">
            <span className="font-medium">{item.name}</span>
            <div className="flex items-center gap-2">
              <button onClick={() => decrement(item.name)} className="px-2 py-1 border rounded">-</button>
              <span>{item.quantity}</span>
              <button onClick={() => increment(item.name)} className="px-2 py-1 border rounded">+</button>
              <button onClick={() => removeItem(item.name)} className="text-red-500 text-sm">🗑</button>
            </div>
          </li>
        ))}
      </ul>

      <p className="mt-4 text-lg font-semibold">총 금액: {total.toLocaleString()}원</p>

      <div className="mt-3 flex flex-col gap-2">
        <button
          onClick={handleCheckout}
          className="bg-black text-white py-2 rounded hover:bg-gray-800"
        >
          결제하기
        </button>
        <button
          onClick={clearCart}
          className="bg-gray-200 text-black py-2 rounded hover:bg-gray-300"
        >
          장바구니 비우기
        </button>
      </div>

      {/* ✅ 결제 모달 */}
      {showModal && (
        <Modal
          isOpen={showModal}
          onClose={() => setShowModal(false)}
          cartItems={cartItems}
        >
          <Checkout 
          totalPrice={total}
          onClose={() => setShowModal(false)} 
          clearCart={clearCart}
           />
        </Modal>
      )}
    </div>
  );
}
