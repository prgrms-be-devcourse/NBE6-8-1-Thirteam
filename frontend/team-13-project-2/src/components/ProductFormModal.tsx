'use client';

import { useState } from 'react';
import { apiFetch } from '@/app/lib/backend/client';

interface ProductFormModalProps {
  initialData: any;
  onClose: () => void;
  onSave: () => void;
}

export default function ProductFormModal({ initialData, onClose, onSave }: ProductFormModalProps) {
    const isEdit = Boolean(initialData?.id);
  
    const [productName, setProductName] = useState(initialData.productName || '');
    const [price, setPrice] = useState(initialData.price || 0);
    const [description, setDescription] = useState(initialData.description || '');
    const [status, setStatus] = useState(initialData.status || 'SALE');
    const [category, setCategory] = useState(initialData.category || 'COFFEE');
    const [stock, setStock] = useState(initialData.stock || 0);
    const [imageUrl, setImageUrl] = useState(initialData.productImage || '');
    const [mainImageFile, setMainImageFile] = useState<File | null>(null);

    // 이미지 파일
    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files.length > 0) {
        const file = e.target.files[0];
        setMainImageFile(file);
    
        // 업로드 API 호출
        const formData = new FormData();
        formData.append('file', file);
    
        try {
          const res = await fetch('/api/upload', {
            method: 'POST',
            body: formData,
          });
    
          const data = await res.json();
          setImageUrl(data.imageUrl); // 업로드된 이미지 URL 상태에 저장
        } catch (error) {
          console.error('이미지 업로드 실패', error);
        }
      } else {
        setMainImageFile(null);
        setImageUrl('');
      }
    };
    
  
    const handleCreate = async () => {
      const payload = {
        productName,
        price,
        description,
        stock,
        status,
        category,
        imageUrl,
      };

      try {
        await apiFetch('/api/v1/products/create', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });
        onSave();
        onClose();
      } catch (err) {
        console.error('저장 실패:', err);
      }
    };


      /* formData 형식의 입력인 경우
        const formData = new FormData();
        formData.append('productName', productName);
        formData.append('price', String(price));
        formData.append('description', description);
        formData.append('category', category);
        formData.append('status', status);
      
        if (mainImageFile) {
            formData.append('mainImage', mainImageFile);
          } else {
            // mainImage는 필수라면 반드시 넣어야 함
            alert("이미지를 선택해주세요.");
            return;
          }
      
        try {
            await apiFetch('/api/v1/products/create', {
                method: 'POST',
                body: formData,
            });
            onSave();
            onClose();
        } catch (err) {
            console.error('저장 실패:', err);
        }
      };
      */
      
      const handleUpdate = async () => {
        const payload = {
          id: initialData.id,
          productName,
          price,
          description,
          stock,
          status,
          category,
          imageUrl,
        };
      
        try {
          await apiFetch('/api/v1/products/update', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
          });
          onSave();
          onClose();
        } catch (err) {
          console.error('상품 수정 실패:', err);
        }
      };
      
      const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
      
        if (isEdit) {
          handleUpdate();
        } else {
          handleCreate();
        }
      };

     return (
      <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded-xl shadow-lg w-full max-w-md">
          <h2 className="text-gray-800 text-xl font-bold mb-4">{isEdit ? '상품 수정' : '상품 등록'}</h2>

          <form onSubmit={handleSubmit} className="flex flex-col gap-2">
          <label className="block text-gray-700 font-semibold">
            상품명
          </label>
          <input
            type="text"
            placeholder="상품명"
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
            required
            className="text-gray-800 border border-gray-300 p-2 rounded w-full mb-1"
/>

          <label className="block text-gray-700 font-semibold">
            가격
          </label>
            <input
              type="number"
              placeholder="가격"
              value={price}
              onChange={(e) => setPrice(Number(e.target.value))}
              required
              className="text-gray-800 border border-gray-300 p-2 rounded mb-1"
            />

          <label className="block text-gray-700 font-semibold">
            상품 설명
          </label>
            <textarea
              placeholder="상품 설명"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="text-gray-800 border border-gray-300 p-2 rounded h-24 mb-1"
            />

          <label className="block text-gray-700 font-semibold">
            재고 수량
          </label>
            <input
              type="number"
              placeholder="재고 수량"
              value={stock}
              onChange={(e) => setStock(Number(e.target.value))}
              required 
              className="text-gray-800 border border-gray-300 p-2 rounded mb-1"
            />

          <label className="block text-gray-700 font-semibold">
            상품 이미지
          </label>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="border border-gray-300 p-2 rounded"
            />

            {imageUrl && (
              <img
                src={imageUrl}
                alt="업로드된 상품 이미지"
                className="mt-2 w-full h-48 object-cover rounded"
              />
            )}

            <div>
              <p className="text-gray-800 font-semibold mb-1">판매 상태</p>
              <div className="flex gap-4">
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="status"
                    value="SALE"
                    checked={status === 'SALE'}
                    onChange={(e) => setStatus(e.target.value)}
                  />
                  판매 중
                </label>
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="status"
                    value="SOLD_OUT"
                    checked={status === 'SOLD_OUT'}
                    onChange={(e) => setStatus(e.target.value)}
                  />
                  품절
                </label>
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="status"
                    value="STOPPED"
                    checked={status === 'STOPPED'}
                    onChange={(e) => setStatus(e.target.value)}
                  />
                  판매 중지
                </label>
              </div>
            </div>

            <div>
              <p className="text-gray-800 font-semibold mb-1">카테고리</p>
              <div className="flex gap-4 flex-wrap">
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="category"
                    value="COFFEE"
                    checked={category === 'COFFEE'}
                    onChange={(e) => setCategory(e.target.value)}
                  />
                  커피
                </label>
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="category"
                    value="JUICE"
                    checked={category === 'JUICE'}
                    onChange={(e) => setCategory(e.target.value)}
                  />
                  쥬스
                </label>
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="category"
                    value="TEA"
                    checked={category === 'TEA'}
                    onChange={(e) => setCategory(e.target.value)}
                  />
                  차
                </label>
                <label className="text-gray-800 flex items-center gap-1">
                  <input
                    type="radio"
                    name="category"
                    value="DESSERT"
                    checked={category === 'DESSERT'}
                    onChange={(e) => setCategory(e.target.value)}
                  />
                  디저트
                </label>
              </div>
            </div>

            <div className="flex justify-end gap-2">
              <button
                type="button"
                onClick={onClose}
                className="text-gray-800 px-4 py-2 border border-gray-300 rounded hover:bg-gray-100"
              >
                취소
              </button>
              <button
                type="submit"
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                저장
              </button>
            </div>
          </form>
        </div>
      </div>
    );
}