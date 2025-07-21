'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { apiFetch } from '../lib/backend/client';

export interface CartItem {
  id: number;
  name: string;
  price: number;
  image: string;
  quantity: number;
  category: string;
}

interface CartContextType {
  cartItems: CartItem[];
  addToCart: (item: Omit<CartItem, 'quantity'>, quantity: number) => Promise<void>;
  updateQuantity: (id: number, quantity: number) => Promise<void>;
  removeFromCart: (id: number) => Promise<void>;
  clearCart: () => Promise<void>;
  reloadCart: () => Promise<void>;  // 필요 시 외부에서 재호출용
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  // 서버에서 장바구니 불러오기 함수
  const reloadCart = async () => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;
      if (!memberId) throw new Error('사용자 정보 없음');

      const wishlistRes = await apiFetch(`/api/v1/wishlist/member/${memberId}`);
      const wishlistItems = wishlistRes.data || [];

      // 상품 상세 정보 동기화
      const detailedItems = await Promise.all(
        wishlistItems.map(async (item: any) => {
          try {
            const productRes = await apiFetch(`/api/v1/products/${item.productId}`);
            const product = productRes.data;
            return {
              id: item.productId,
              name: item.productName,
              price: item.productPrice,
              quantity: item.quantity ?? 0,
              image: product.productImage || '',
              category: product.category || '',
            };
          } catch {
            return {
              id: item.productId,
              name: item.productName,
              price: item.productPrice,
              quantity: item.quantity ?? 0,
              image: '',
              category: '',
            };
          }
        })
      );

      setCartItems(detailedItems);
    } catch (error) {
      console.error('장바구니 불러오기 실패', error);
    }
  };

  // 초기 로드
  useEffect(() => {
    const checkAndLoadCart = async () => {
      try {
        const memberRes = await apiFetch('/api/v1/members/me');
        const memberId = memberRes.data?.id;
        if (memberId) {
          await reloadCart();
        } else {
          console.info('비로그인 상태: 장바구니 로드 생략');
        }
      } catch (error) {
        console.info('비로그인 상태: 장바구니 로드 생략');
      }
    };
    checkAndLoadCart();
  }, []);

  // 수량 변경
  const updateQuantity = async (id: number, quantity: number) => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;
      if (!memberId) throw new Error('사용자 정보 없음');

      await apiFetch('/api/v1/wishlist/quantity', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memberId, productId: id, newQuantity: quantity }),
      });

      // 상태 업데이트
      setCartItems((prev) =>
        prev.map((item) => (item.id === id ? { ...item, quantity } : item))
      );
    } catch (error) {
      console.error('수량 변경 실패', error);
    }
  };

  // 아이템 삭제
  const removeFromCart = async (id: number) => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;
      if (!memberId) throw new Error('사용자 정보 없음');

      await apiFetch('/api/v1/wishlist', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ memberId, productId: id }),
      });

      setCartItems((prev) => prev.filter((item) => item.id !== id));
    } catch (error) {
      console.error('상품 삭제 실패', error);
    }
  };

  // 장바구니 비우기
  const clearCart = async () => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;
      if (!memberId) throw new Error('사용자 정보 없음');

      // 서버 전체 삭제 API가 없으면 클라이언트만 초기화하는 정도
      await apiFetch(`/api/v1/wishlist/member/${memberId}`, {
        method: 'DELETE'
      });
      setCartItems([]);
    } catch (error) {
      console.error('장바구니 비우기 실패', error);
    }
  };

  const addToCart = async (item: Omit<CartItem, 'quantity'>, quantity: number) => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;
      if (!memberId) throw new Error('사용자 정보 없음');

      // 장바구니에 이미 있는지 체크
      const wishlistRes = await apiFetch(`/api/v1/wishlist/member/${memberId}`);
      const existing = wishlistRes.data.find((i: any) => i.productId === item.id);

      if (existing) {
        const newQuantity = existing.quantity + quantity;
        await apiFetch('/api/v1/wishlist/quantity', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ memberId, productId: item.id, newQuantity }),
        });
        setCartItems((prev) =>
          prev.map((i) => (i.id === item.id ? { ...i, quantity: newQuantity } : i))
        );
      } else {
        await apiFetch('/api/v1/wishlist', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ memberId, productId: item.id, quantity }),
        });
        // 새 아이템 추가를 위해 새로고침 권장
        reloadCart();
      }
    } catch (error) {
      console.error('장바구니 추가 실패', error);
    }
  };

  return (
    <CartContext.Provider
      value={{
        cartItems,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        reloadCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) throw new Error('useCart는 CartProvider 내부에서 사용해야 합니다.');
  return context;
};
