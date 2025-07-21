'use client';

import Link from 'next/link';
import AuthNav from './AuthNav';
import CartIcon from './CartIcon';

export default function Header() {
  return (
    <header className="bg-[#d9c1a3] text-white shadow z-50">
      <nav className="flex justify-between items-center max-w-5xl mx-auto p-4 relative">
        <div className="flex items-center gap-2">
          <span className="text-3xl">☕</span>
          <Link href="/" className="text-xl font-bold text-amber-900">
            Grids & Circles
          </Link>
        </div>

        <div className="flex items-center gap-4">
          <Link
            href="/menu"
            className="hover:bg-[#8c7051] px-3 py-2 rounded-lg transition-colors duration-200"
          >
            메뉴
          </Link>

          <Link
            href="/admin"
            className="hover:bg-[#8c7051] px-3 py-2 rounded-lg transition-colors duration-200"
          >
            관리
          </Link>

          <CartIcon /> {/* 장바구니 아이콘 */}
          <AuthNav />
        </div>
      </nav>
    </header>
  );
}
