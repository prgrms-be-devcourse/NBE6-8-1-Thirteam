'use client';

import { useRouter } from 'next/navigation';
import { useState, useEffect, useMemo } from 'react';
import { apiFetch } from '../lib/backend/client';

type User = {
    id: number;
    username: string;
    role: string;
  };

// OrderItemDto 타입
type OrderItem = {
    id: number;
    productName: string;
    quantity: number;
    productPrice: number;
    totalPrice: number;
};

// OrderDto 타입
type Order = {
    id: number;
    createDate: string;
    modifyDate: string;
    totalPrice: number;
    address: string;
    order_status: string;
    orderItems: OrderItem[];
};

// API 응답 타입
type OrdersApiResponse = {
    resultCode: string;
    msg: string;
    data: Order[];
};

export default function AdminPage() {
    const router = useRouter();

    const [user, setUser] = useState<User | null>(null);
    const [accessDenied, setAccessDenied] = useState(false);

    // 주문 상태
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [statusFilter, setStatusFilter] = useState('ALL');

    // 사용자 정보 요청.
    useEffect(() => {
        const fetchUser = async () => {
          try {
            const res = await apiFetch('/api/v1/members/me');
            if (res.data?.role !== 'ADMIN') {
              setAccessDenied(true);
              setTimeout(() => router.push('/'), 5000); // 5초 후 홈으로 이동
            } else {
              setUser(res);
            }
          } catch (err) {
            setAccessDenied(true);
            setTimeout(() => router.push('/'), 5000);
          }
        };
    
        fetchUser();
      }, [router]);

    // 주문목록 API에서 받아오기
    useEffect(() => {
        setLoading(true);
        setError(null);
        fetch('http://localhost:8080/api/v1/orders')
            .then((res) => {
                if (!res.ok) throw new Error('주문 목록 불러오기 실패');
                return res.json();
            })
            .then((data: OrdersApiResponse) => {
                setOrders(Array.isArray(data.data) ? data.data : []);
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    // order_status 옵션 (중복 없는 실제 주문에서 추출)
    const orderStatusList = useMemo(() => {
        const set = new Set<string>();
        orders.forEach((o) => set.add(o.order_status));
        return Array.from(set);
    }, [orders]);

    // 주문 상태별 필터
    const filteredOrders =
        statusFilter === 'ALL'
            ? orders
            : orders.filter((order) => order.order_status === statusFilter);

    if (accessDenied) {
        return (
            <div className="flex flex-col items-center justify-center h-screen text-center">
            <h1 className="text-3xl font-bold mb-4">⚠️ 접근 제한 ⚠️</h1>
            <p className="text-lg">관리자만 접근 가능한 페이지입니다.</p>
            <p className="text-sm text-gray-500 mt-2">5초 후 홈으로 이동합니다...</p>
            </div>
        );
    }

    return (
        <div className="p-8 max-w-5xl mx-auto">
            {/* 1. 커피 메뉴 조정 페이지로 이동 */}
            <section className="mb-12">
                <h1 className="text-2xl font-bold mb-6">관리자 페이지</h1>
                <button
                    className="px-6 py-3 bg-[#6b4f3b] text-white rounded-lg hover:bg-[#8c7051] transition"
                    onClick={() => router.push('/admin/edit')}
                >
                    커피 메뉴 조정 페이지로 이동
                </button>
            </section>

            {/* 2. 주문 목록 */}
            <section>
                <div className="flex items-center gap-4 mb-6">
                    <h2 className="text-xl font-bold">현재 들어온 주문 목록</h2>
                    {/* 상태 필터 */}
                    <select
                        className="ml-4 border px-2 py-1 rounded"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                    >
                        <option value="ALL">전체</option>
                        {orderStatusList.map((status) => (
                            <option key={status} value={status}>
                                {status}
                            </option>
                        ))}
                    </select>
                </div>

                {loading && <p>주문 목록을 불러오는 중...</p>}
                {error && (
                    <p className="text-red-500">에러: {error}</p>
                )}
                {!loading && !error && filteredOrders.length === 0 && (
                    <p>해당 주문 상태의 내역이 없습니다.</p>
                )}
                <ul className="space-y-6">
                    {filteredOrders.map((order) => (
                        <li key={order.id} className="border rounded-lg shadow p-5">
                            <div className="flex justify-between items-center mb-2">
                                <div>
                                    <span className="font-semibold">주문 번호:</span> {order.id}
                                </div>
                                <span className="bg-gray-100 px-2 py-1 rounded font-semibold text-sm text-black">
                  {order.order_status}
                </span>
                            </div>
                            <div className="mb-2">
                                <span className="font-semibold">주소:</span> {order.address}
                            </div>
                            <div className="mb-2">
                                <span className="font-semibold">총 금액:</span>{' '}
                                {order.totalPrice.toLocaleString()}원
                            </div>
                            <div className="mb-2">
                                <span className="font-semibold">주문일:</span>{' '}
                                {new Date(order.createDate).toLocaleString()}
                            </div>
                            <div className="mb-2">
                                <span className="font-semibold">수정일:</span>{' '}
                                {new Date(order.modifyDate).toLocaleString()}
                            </div>
                            <div className="mt-3">
                                <span className="font-semibold">주문 항목:</span>
                                <ul className="list-disc ml-6 mt-2">
                                    {order.orderItems.map((item) => (
                                        <li key={item.id}>
                                            <span className="font-semibold">{item.productName}</span> (
                                            {item.productPrice.toLocaleString()}원)
                                            × {item.quantity} = {item.totalPrice.toLocaleString()}원
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </li>
                    ))}
                </ul>
            </section>
        </div>
    );
}
