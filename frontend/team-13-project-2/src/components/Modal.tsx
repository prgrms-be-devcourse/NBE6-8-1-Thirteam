'use client';

import React, { useEffect } from 'react';
import { MenuItem } from './Cart';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  cartItems: MenuItem[];
  children: React.ReactNode; // ✅ children 추가
}

export default function Modal({ isOpen, onClose, cartItems, children }: ModalProps) {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleEsc);
    return () => window.removeEventListener('keydown', handleEsc);
  }, [onClose]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white w-[960px] max-h-[90vh] overflow-y-auto rounded-lg shadow-lg p-8 relative flex flex-col gap-6 sm:flex-row sm:gap-10">
        {/* ✏️ 왼쪽: 장바구니 아이템 리스트 */}
        <div className="w-full sm:w-1/2">
          <h2 className="text-gray-800 text-xl font-bold mb-4">🧾 주문 내역</h2>
          <ul className="space-y-3 text-base max-h-[50vh] overflow-y-auto">
            {cartItems.map((item) => (
              <li key={item.name} className="text-gray-800 flex justify-between border-b pb-1">
                <span>{item.name} x {item.quantity}</span>
                <span>{(item.price * item.quantity).toLocaleString()}원</span>
              </li>
            ))}
          </ul>
        </div>

        {/* ✅ 오른쪽: children 영역 (결제 등) */}
        <div className="text-gray-800 w-full sm:w-1/2 flex flex-col justify-between">
          <div>{React.isValidElement(children) && React.cloneElement(children as React.ReactElement<any>, { onClose })}
          </div>

          {/* 🔻 비어 있는 하단 공간을 UI로 채움 */}
          <div className="text-gray-800 mt-6 text-sm text-gray-500 text-center">
            결제 전 주문 내역을 꼭 확인해주세요 ☕
          </div>
        </div>

        {/* ❌ 닫기 버튼 */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-2xl text-gray-500 hover:text-gray-800"
          aria-label="닫기"
        >
          &times;
        </button>
      </div>
    </div>
  );
}
