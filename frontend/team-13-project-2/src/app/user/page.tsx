'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '../lib/backend/client';

interface OrderItem {
  id: number;
  productName: string;
  productImage: string;
  productPrice: number;
  quantity: number;
  totalPrice: number;
}

interface Order {
  id: number;
  address: string;
  order_status: string;
  totalPrice: number;
  orderItems: OrderItem[];
}

export default function UserOrderListPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [filteredOrders, setFilteredOrders] = useState<Order[]>([]);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchOrders();
  }, []);

  /*
  const [orders, setOrders] = useState<any[]>([
    {
      id: 1,
      productName: '크리스탈라이트 레몬 아이스티',
      productImage: '/images/product1.jpg',
      status: '배송 준비중',
      deliveryStatus: '배송 준비중',
      totalPrice: 20000,
      deliveryDate: '7/15(화) 도착'
    },
    {
      id: 2,
      productName: '코카콜라',
      productImage: '/images/product2.jpg',
      status: '배송 완료',
      deliveryStatus: '배송 완료',
      totalPrice: 5000,
      deliveryDate: '7/10(토) 도착'
    },
    {
      id: 3,
      productName: '스타벅스 아이스 아메리카노',
      productImage: '/images/product3.jpg',
      status: '배송 중',
      deliveryStatus: '배송 중',
      totalPrice: 4500,
      deliveryDate: '7/13(화) 도착'
    },
  ]);  // 주문 내역 상태
  */

  const fetchOrders = async () => {
    try {
      const memberRes = await apiFetch('/api/v1/members/me');
      const memberId = memberRes.data?.id;

      if(!memberId) throw new Error("잘못된 사용자 정보입니다.");

      const orderRes = await apiFetch('/api/v1/orders/my');
      const data = orderRes.data || [];

      console.log(data);

      const formatted = data.map((order: Order) => ({
        id: order.id,
        address: order.address,
        order_status: order.order_status,
        totalPrice: order.totalPrice,
        orderItems: order.orderItems.map((item: OrderItem) => ({
          id: item.id,
          productName: item.productName,
          productImage: item.productImage,
          productPrice: item.productPrice,
          quantity: item.quantity,
          totalPrice: item.totalPrice
        }))
      }));

      console.log(formatted);

      setOrders(formatted);
      setFilteredOrders(formatted);
    } catch (err) {
      console.error("주문 목록 불러오기 실패.");
    }
  }

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  const handleSearch = () => {
    const query = searchQuery.toLowerCase();
    const filtered = orders.filter(order =>
      order.orderItems.some(item =>
        item.productName.toLowerCase().includes(query)
      )
    );
    setFilteredOrders(filtered);
  };

  return (
    <div className="p-6">
      {/* 검색창 */}
      <div className="flex items-center mb-6">
        <input
          type="text"
          placeholder="주문한 상품을 검색할 수 있어요!"
          value={searchQuery}
          onChange={handleSearchChange}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          onClick={handleSearch}
          className="ml-3 px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700"
        >
          검색
        </button>
      </div>

      {/* 주문 목록 */}
      <div className="space-y-8">
        {filteredOrders.length === 0 ? (
          <p className="text-gray-500">검색된 주문이 없습니다.</p>
        ) : (
          filteredOrders.map(order => (
            <div
              key={order.id}
              className="border-b pb-4"
            >
              <p className="font-bold mb-2">배송지: {order.address}</p>
              <p className="font-bold 0 mb-4">주문 상태: {" "}
                  {order.order_status === "ORDERED"
                  ? "상품 준비중"
                  : order.order_status === "DELIVERED"
                  ? "상품 배송중"
                  : order.order_status}
              </p>

              {order.orderItems.map(item => (
                <div
                  key={item.id}
                  className="flex justify-between items-center mb-4"
                >
                  <div className="flex gap-4 items-center">
                    <img
                      src={item.productImage}
                      alt={item.productName}
                      className="w-16 h-16 object-cover rounded"
                    />
                    <div>
                      <h3 className="font-semibold">{item.productName}</h3>
                      <p className="text-sm text-gray-600">수량: {item.quantity}</p>
                      <p className="text-sm text-gray-600">가격: {item.totalPrice.toLocaleString()}원</p>
                    </div>
                  </div>
                </div>
              ))}

              <div className="text-right text-md font-bold">
                총 주문 금액: {order.totalPrice.toLocaleString()}원
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
