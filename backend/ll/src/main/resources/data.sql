-- 커피
INSERT INTO product (product_name, price, description, product_image, stock, category, status)
VALUES
    ('아이스 라떼', 4800, '부드러운 우유와 진한 에스프레소가 어우러진 시원한 라떼', '/images/IcedLatte.png', 100, 'COFFEE', 'SALE'),
    ('에스프레소', 3000, '진한 원두의 풍미와 깊은 맛', '/images/Espresso.png', 200, 'COFFEE', 'SALE'),
    ('아메리카노', 3500, '진한 에스프레소에 뜨거운 물을 더한 커피', '/images/Americano.png', 180, 'COFFEE', 'SALE'),
    ('아이스 아메리카노', 4000, '시원한 얼음과 진한 에스프레소가 어우러진 커피', '/images/IcedAmericano.png', 150, 'COFFEE', 'SALE');

-- 쥬스
INSERT INTO product (product_name, price, description, product_image, stock, category, status)
VALUES
    ('오렌지 주스', 4300, '신선한 오렌지의 상큼함을 담은 피로회복용 건강 주스', '/images/OrangeJuice.png', 80, 'JUICE', 'SALE'),
    ('망고 주스', 4500, '열대의 달콤함이 가득 담긴 진한 망고주스', '/images/MangoJuice.png', 60, 'JUICE', 'SALE'),
    ('딸기 주스', 4700, '국산 딸기를 갈아 만든 달콤한 딸기주스', '/images/StrawberryJuice.png', 70, 'JUICE', 'SALE'),
    ('케일 주스', 4800, '건강한 채소의 맛을 담은 케일주스', '/images/KaleJuice.png', 50, 'JUICE', 'SALE');

-- 티
INSERT INTO product (product_name, price, description, product_image, stock, category, status)
VALUES
    ('민트 티', 4200, '상쾌하고 시원한 민트의 향과 맛', '/images/MintTea.png', 60, 'TEA', 'SALE'),
    ('그린티', 4100, '진한 녹차 잎의 향긋함이 느껴지는 그린티', '/images/GreenTea.png', 75, 'TEA', 'SALE'),
    ('아이스 유스베리 티', 4500, '각종 베리의 새콤달콤한 맛이 담긴 아이스티', '/images/IcedYouthberryTea.png', 70, 'TEA', 'SALE'),
    ('아이스 얼그레이 티', 4300, '진한 얼그레이 향과 시원함이 어우러진 클래식 차', '/images/IceEarlGreyTea.png', 65, 'TEA', 'SALE');

--디저트
INSERT INTO product (product_name, price, description, product_image, stock, category, status)
VALUES
    ('바스크 초코 치즈케이크', 5300, '진한 초콜릿과 치즈의 부드러움이 조화를 이룬 바스크 스타일의 치즈케이크', '/images/BasqueChocoCheeseCake.png', 48, 'DESSERT', 'SALE'),
    ('솔트 브레드', 2300, '겉은 바삭, 속은 촉촉하게 구운 고소한 소금빵', '/images/SaltedBread.png', 120, 'DESSERT', 'SALE'),
    ('글레이즈드 도넛', 2000, '향긋한 바닐라 글레이즈로 코팅된 클래식 도넛', '/images/GlazedDoughnut.png', 85, 'DESSERT', 'SALE'),
    ('블루베리 마블 치즈케이크', 5600, '리얼 블루베리 퓨레와 치즈의 조화로운 마블 치즈케이크', '/images/BlueberryMarbleCheeseCake.png', 42, 'DESSERT', 'SALE');
