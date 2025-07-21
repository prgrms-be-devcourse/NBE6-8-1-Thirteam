'use client';

import { useCart } from '@/app/contexts/CartContext';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Modal from '@/components/Modal';
import Checkout from '@/components/Checkout';

export default function CartPage() {
  const { cartItems, updateQuantity, removeFromCart, clearCart } = useCart();
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const router = useRouter();

  const total = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <div className="p-6">
      <button
        onClick={() => router.push('/menu')}
        className="mb-4 px-4 py-2 border rounded font-semibold text-[#8c7051] hover:bg-[#8c7051] hover:text-white"
      >
        상품 추가하기
      </button>

      <h1 className="text-2xl font-bold mb-4">🛒 장바구니</h1>

      {cartItems.length === 0 ? (
        <p className="text-gray-600">장바구니가 비어 있습니다.</p>
      ) : (
        <>
          <div className="space-y-4">
            {cartItems.map((item) => (
              <div key={item.id} className="flex justify-between items-center border p-4 rounded-lg shadow">
                <div className="flex items-center space-x-4">
                  <img
                    src={item.image || '/placeholder.png'}
                    alt={item.name}
                    className="w-16 h-16 rounded object-cover"
                  />
                  <div>
                    <p className="font-semibold">{item.name}</p>
                    <div className="flex items-center gap-2 mt-1">
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="text-gray-800 px-2 py-1 bg-white border rounded"
                        disabled={item.quantity <= 1}
                      >
                        -
                      </button>
                      <span className="border px-3 py-1 rounded">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="text-gray-800 px-2 py-1 bg-white border rounded"
                      >
                        +
                      </button>
                    </div>
                    <p className="font-semibold mt-1">{item.price.toLocaleString()}원</p>
                  </div>
                </div>

                <div className="flex flex-col items-end space-y-2">
                  <p className="font-bold">{(item.price * item.quantity).toLocaleString()}원</p>
                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="text-red-500 hover:text-red-700 text-sm"
                  >
                    🗑 삭제
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="flex justify-end space-x-4 mt-6">
            <button
              onClick={clearCart}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              장바구니 비우기
            </button>
            <button
              onClick={() => setIsCheckoutOpen(true)}
              className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              결제하기
            </button>
          </div>

          <div className="text-right text-xl font-bold mt-6">
            총 합계: {total.toLocaleString()}원
          </div>
        </>
      )}

      {isCheckoutOpen && (
        <Modal
          isOpen={isCheckoutOpen}
          onClose={() => setIsCheckoutOpen(false)}
          cartItems={cartItems}
        >
          <Checkout 
          totalPrice={total}
          onClose={() => setIsCheckoutOpen(false)}
          clearCart={clearCart}
          />
        </Modal>
      )}
    </div>
  );
}
