'use client';

import React, { useEffect, useState } from 'react';
import { apiFetch } from '@/app/lib/backend/client';
import { useRouter } from 'next/navigation';
import { toast } from "react-toastify";

interface CheckoutProps {
  totalPrice: number;
  onClose: () => void;
  clearCart: () => void;
}

interface WishlistItem {
  productId: number;
  quantity: number;
  // 필요한 다른 필드들도 여기에 추가 가능
}

export default function Checkout({ totalPrice, onClose, clearCart }: CheckoutProps) {
  const [address, setAddress] = useState('');
  const router = useRouter();

  useEffect(() => {
    const fetchAddress = async () => {
      try {
        const res = await apiFetch('/api/v1/members/me');
        if (!res) throw new Error('사용자 정보를 가져오는 데 실패했습니다');

        const fetchedAddress = res.data?.address;
        setAddress(fetchedAddress);
      } catch (err) {
        console.error('주소 정보 불러오기 실패', err);
      }
    };

    fetchAddress();
  }, []);

  const handleCheckOut = async () => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;

      if (!memberId) throw new Error('사용자 정보 없음');

      const wishlistRes = await apiFetch(`/api/v1/wishlist/member/${memberId}`);
      const wishlistItems: WishlistItem[] = wishlistRes.data || []

      const payload = {
        productIds: wishlistItems.map(item => item.productId),
        quantities: wishlistItems.map(item => item.quantity),
        address
      };

      if (wishlistItems.length === 0) {
        alert('장바구니에 상품이 없습니다.');
        return;
      }

      await apiFetch('/api/v1/orders', {
        method: 'POST',
        body: JSON.stringify(payload),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      await apiFetch(`/api/v1/wishlist/member/${memberId}`, {
        method: 'DELETE'
      });

      toast.success("주문이 완료되었어요!");
      onClose();
      clearCart();
      router.refresh();
      
      } catch (error: any) {
        switch (error.resultCode) {
          case "400-6":
            toast.error(error.msg);
            onClose();
            break;
          default:
            console.error('장바구니 비우기 실패', error);
            break;
        }
      }
    }

  return (
    <div>
      <h2 className="text-2xl font-bold mb-4">결제 정보</h2>
      <div className="space-y-3">
        {/* 이메일 필드와 우편번호 필드를 제거하고 주소만 남깁니다 */}
        <input
          type="text"
          placeholder="주소"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          className="text-gray-800 w-full border p-2 rounded"
        />
      </div>
      <p className="text-sm text-gray-600 mt-3">당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.</p>
      <div className="flex justify-between items-center mt-4">
        <span className="font-bold text-lg">총금액</span>
        <span className="text-xl font-semibold">{totalPrice.toLocaleString()}원</span>
      </div>
      <button 
      onClick={handleCheckOut}
      className="mt-4 w-full bg-black text-white py-2 rounded hover:bg-gray-800">
        
        {/*결제 버튼 누를 시, /api/v1/wishlist/member/{memberId}를 통해 장바구니를 비움, 장바구니 항목을 주문으로 이동 */}
        결제하기
      </button>
    </div>
  );
}
