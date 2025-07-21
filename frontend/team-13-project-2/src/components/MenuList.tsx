'use client';

import { useState } from 'react';

type MenuItem = {
  id: number;
  name: string;
  price: number;
  image: string;
  description?: string;
};

interface MenuListProps {
  menuItems: MenuItem[];
  setSelectedItem: (item: MenuItem) => void;
}

export default function MenuList({ menuItems, setSelectedItem }: MenuListProps) {
  return (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
      {menuItems.map((item) => (
        <div
          key={item.id}
          className="p-4 bg-white border border-gray-200 rounded-lg shadow hover:shadow-md transition cursor-pointer"
          onClick={() => setSelectedItem(item)}
        >
          <img
            src={item.image}
            alt={item.name}
            className="w-full h-40 object-cover rounded mb-4"
          />
          <div className="text-center font-semibold text-lg text-gray-800">
            {item.name}
          </div>
          <div className="text-center text-sm text-gray-500 mt-1">
            {item.price.toLocaleString()}원
          </div>
        </div>
      ))}
    </div>
  );
}
