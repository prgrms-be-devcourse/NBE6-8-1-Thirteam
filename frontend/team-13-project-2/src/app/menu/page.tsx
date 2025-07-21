'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/app/lib/backend/client';
import MenuItem from '@/components/MenuItem';
import MenuDetailModal from '@/components/MenuDetailModal';

/* 예제 데이터 -> REST API를 사용하는 방식으로 변경.
const mockMenu = [
  {
    id: 1,
    name: '아메리카노',
    price: 3000,
    image: '/images/americano.jpg',
    description: '진한 에스프레소에 물을 더한 커피입니다.',
  },
  {
    id: 2,
    name: '카페라떼',
    price: 4000,
    image: '/images/latte.jpg',
    description: '부드러운 우유와 조화로운 커피입니다.',
  },
  {
    id: 3,
    name: '바닐라라떼',
    price: 4500,
    image: '/images/vanilla.jpg',
    description: '달콤한 바닐라 시럽이 들어간 부드러운 라떼입니다.',
  },
];
*/ 

export default function MenuPage() {
  const [menu, setMenu] = useState<any[]>([]);
  const [selectedItem, setSelectedItem] = useState<any>(null);

  useEffect(() => { // 상품 목록을 받아오는 REST API.
    const fetchMenu = async () => {
      try {
        const res = await apiFetch('/api/v1/products');
        const products = res.data || [];

        const mappedMenu = products
        .filter((product: any) => product.status === "SALE") // SALE인 경우만
        .map((product: any) => ({
          id: product.id,
          name: product.productName,
          price: product.price,
          status: product.status,
          image: product.productImage,
          description: product.description,
        }));

        setMenu(mappedMenu);
      } catch (err) {
        console.error('메뉴 불러오기 실패:', err);
      }
    };

    fetchMenu();
  }, []);

  const handleAddToCart = (item: any, quantity: number) => {
    console.log('장바구니에 담기:', item.name, '수량:', quantity);
    // TODO: 추후 CartContext에 연동
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">커피 메뉴</h1>

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {menu.map((item) => (
          <MenuItem
            key={item.id}
            item={item}
            onClick={() => setSelectedItem(item)}
          />
        ))}
      </div>

      {selectedItem && (
        <MenuDetailModal
          item={selectedItem}
          onClose={() => setSelectedItem(null)}
        />
      )}
    </div>
  );
}
