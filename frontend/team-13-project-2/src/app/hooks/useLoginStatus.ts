'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/app/lib/backend/client';

export function useLoginStatus() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const checkLogin = async () => {
      try {
        const res = await apiFetch('/api/v1/members/me');
        if (res.resultCode.startsWith('202')) {
          setIsLoggedIn(true);
        } else {
          setIsLoggedIn(false);
        }
      } catch {
        setIsLoggedIn(false);
      }
    };
    checkLogin();
  }, []);

  return isLoggedIn;
}
