// components/MenuItem.tsx

import React from 'react';

interface MenuItemProps {
  item: {
    id: number;
    name: string;
    description: string;
    price: number;
    image: string;
  };
  onClick: () => void;
}

export default function MenuItem({ item, onClick }: MenuItemProps) {
  return (
    <div
      className="border p-4 rounded-lg shadow hover:cursor-pointer hover:scale-105 transition"
      onClick={onClick}
    >
      <img
        src={item.image}
        alt={item.name}
        className="w-full h-32 object-cover mb-2 rounded"
      />
      <h2 className="font-bold">{item.name}</h2>
      <p className="text-sm text-gray-600">{item.description}</p>
      <p className="font-semibold mt-1">{item.price.toLocaleString()}원</p>
    </div>
  );
}
