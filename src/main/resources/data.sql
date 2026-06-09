-- ============================================================
-- JEUX DE DONNÉES — EcommerceAPI (MySQL)
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE panier_item;
TRUNCATE TABLE command_item;
TRUNCATE TABLE panier;
TRUNCATE TABLE command;
TRUNCATE TABLE livre_cat;
TRUNCATE TABLE livre;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE app_user;
TRUNCATE TABLE autheur;
TRUNCATE TABLE categorie;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. CATÉGORIES
-- ============================================================
INSERT INTO categorie (id, nom_cat) VALUES
(1,  'Science-Fiction'),
(2,  'Dystopie'),
(3,  'Classique'),
(4,  'Aventure spatiale'),
(5,  'Philosophie'),
(6,  'Policier'),
(7,  'Fantastique'),
(8,  'Horreur'),
(9,  'Biographie'),
(10, 'Aventure');

ALTER TABLE categorie AUTO_INCREMENT = 11;

-- ============================================================
-- 2. AUTEURS
-- ============================================================
INSERT INTO autheur (id, nom, prenom) VALUES
(1,  'Orwell',   'George'),
(2,  'Asimov',   'Isaac'),
(3,  'Herbert',  'Frank'),
(4,  'Barjavel', 'René'),
(5,  'Christie', 'Agatha'),
(6,  'Tolkien',  'J.R.R.'),
(7,  'King',     'Stephen'),
(8,  'Hugo',     'Victor'),
(9,  'Verne',    'Jules'),
(10, 'Camus',    'Albert');

ALTER TABLE autheur AUTO_INCREMENT = 11;

-- ============================================================
-- 3. LIVRES
-- ============================================================
INSERT INTO livre (id, titre, prix, resume, out_date, quantite_stock, quantite_vendue, autheur_id, open_couverture, close_couverture) VALUES
(1,  '1984', 15.99,
     'Dans un État totalitaire dominé par Big Brother, Winston Smith ose penser par lui-même et tomber amoureux — deux actes punissables de mort.',
     '1949-06-08', 50, 120, 1,
     'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780451524935-M.jpg'),

(2,  'La Ferme des animaux', 12.50,
     'Les animaux d''une ferme chassent leur maître humain pour créer une utopie égalitaire — qui dégénère rapidement en dictature porcine.',
     '1945-08-17', 35, 85, 1,
     'https://covers.openlibrary.org/b/isbn/9780452284241-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780452284241-M.jpg'),

(3,  'Fondation', 18.00,
     'Hari Seldon prédit par la psychohistoire l''effondrement de l''Empire Galactique.',
     '1951-06-01', 40, 60, 2,
     'https://covers.openlibrary.org/b/isbn/9780553382570-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780553382570-M.jpg'),

(4,  'Les Robots', 14.50,
     'À travers une série de nouvelles, Asimov explore les Trois Lois de la Robotique.',
     '1950-12-02', 30, 45, 2,
     'https://covers.openlibrary.org/b/isbn/9780553294385-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780553294385-M.jpg'),

(5,  'Dune', 24.50,
     'Sur la planète désertique Arrakis, le jeune Paul Atreides devient le messie d''un peuple opprimé.',
     '1965-08-01', 60, 200, 3,
     'https://covers.openlibrary.org/b/isbn/9780441013593-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780441013593-M.jpg'),

(6,  'Le Messie de Dune', 19.99,
     'Paul Atreides supporte mal le poids de sa divinité.',
     '1969-10-01', 25, 90, 3,
     'https://covers.openlibrary.org/b/isbn/9780441015610-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780441015610-M.jpg'),

(7,  'La Nuit des temps', 14.20,
     'Deux êtres humains endormis depuis 900 000 ans sont découverts dans le permafrost antarctique.',
     '1968-04-01', 45, 70, 4,
     'https://covers.openlibrary.org/b/isbn/9782266028509-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9782266028509-M.jpg'),

(8,  'Ravage', 11.00,
     'Une panne mondiale de l''électricité précipite l''humanité dans le chaos.',
     '1943-01-01', 20, 55, 4,
     'https://covers.openlibrary.org/b/isbn/9782266281485-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9782266281485-M.jpg'),

(9,  'Le Crime de l''Orient-Express', 9.50,
     'Hercule Poirot est bloqué dans l''Orient-Express par une tempête de neige.',
     '1934-01-01', 55, 180, 5,
     'https://covers.openlibrary.org/b/isbn/9780007119318-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780007119318-M.jpg'),

(10, 'Ils étaient dix', 10.00,
     'Dix inconnus invités sur une île isolée meurent selon les vers d''une comptine.',
     '1939-11-06', 50, 160, 5,
     'https://covers.openlibrary.org/b/isbn/9780062073488-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780062073488-M.jpg'),

(11, 'Le Seigneur des Anneaux', 30.00,
     'Le hobbit Frodon entreprend la périlleuse quête vers la Montagne du Destin.',
     '1954-07-29', 70, 250, 6,
     'https://covers.openlibrary.org/b/isbn/9780618640157-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780618640157-M.jpg'),

(12, 'Le Hobbit', 15.00,
     'Le paisible hobbit Bilbon Sacquet est entraîné dans une aventure épique.',
     '1937-09-21', 65, 210, 6,
     'https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780547928227-M.jpg'),

(13, 'Shining', 16.80,
     'L''hôtel Overlook hanté a de terribles projets pour la famille Torrance.',
     '1977-01-28', 30, 100, 7,
     'https://covers.openlibrary.org/b/isbn/9780307743657-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780307743657-M.jpg'),

(14, 'Ça', 22.00,
     'Sept enfants affrontent une entité maléfique sous les traits du clown Pennywise.',
     '1986-09-15', 35, 130, 7,
     'https://covers.openlibrary.org/b/isbn/9781501156700-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9781501156700-M.jpg'),

(15, 'Les Misérables', 12.00,
     'Jean Valjean, ancien forçat en quête de rédemption, croise le destin de la petite Cosette.',
     '1862-01-01', 40, 95, 8,
     'https://covers.openlibrary.org/b/isbn/9780451419439-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780451419439-M.jpg'),

(16, 'Notre-Dame de Paris', 11.50,
     'Quasimodo s''éprend de la belle Esmeralda dans le Paris médiéval.',
     '1831-01-01', 25, 60, 8,
     'https://covers.openlibrary.org/b/isbn/9780140443530-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780140443530-M.jpg'),

(17, 'Vingt mille lieues sous les mers', 13.00,
     'Le professeur Aronnax est capturé par le mystérieux capitaine Nemo.',
     '1870-01-01', 35, 75, 9,
     'https://covers.openlibrary.org/b/isbn/9780140440218-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780140440218-M.jpg'),

(18, 'Le Tour du monde en 80 jours', 10.50,
     'Phileas Fogg parie qu''il peut faire le tour du monde en 80 jours.',
     '1872-01-01', 45, 110, 9,
     'https://covers.openlibrary.org/b/isbn/9780140440461-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780140440461-M.jpg'),

(19, 'L''Étranger', 9.00,
     'Meursault incarne l''absurdité de la condition humaine dans l''Algérie coloniale.',
     '1942-01-01', 55, 140, 10,
     'https://covers.openlibrary.org/b/isbn/9780679720201-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780679720201-M.jpg'),

(20, 'La Peste', 10.00,
     'Le docteur Rieux combat une épidémie de peste à Oran au péril de sa vie.',
     '1947-06-10', 50, 120, 10,
     'https://covers.openlibrary.org/b/isbn/9780679720218-L.jpg',
     'https://covers.openlibrary.org/b/isbn/9780679720218-M.jpg');

ALTER TABLE livre AUTO_INCREMENT = 21;

-- ============================================================
-- 4. ASSOCIATIONS LIVRE ↔ CATÉGORIE
-- ============================================================
INSERT INTO livre_cat (livre_id, cat_id) VALUES
(1,1),(1,2),(2,3),(3,1),(3,4),(4,1),(5,1),(5,4),(5,5),
(6,1),(6,4),(7,1),(7,3),(8,1),(8,2),(9,6),(10,6),
(11,7),(11,10),(12,7),(12,10),(13,8),(14,8),(15,3),
(16,3),(17,10),(17,4),(18,10),(19,3),(19,5),(20,3),(20,5);

-- ============================================================
-- 5. UTILISATEURS
-- ============================================================
INSERT INTO app_user (id, nom, prenom, email, password, adresse, date_naissance) VALUES
('550e8400-e29b-41d4-a716-446655440001','Takoumbo','Zidane','admin@zizou.com',
 '$2b$10$dFjHEIbSNQeoYUOh6ZntNeTolXKudKsYEerbrP/Ves63fQySF9Zvu',
 '12 rue de Paris, 75001 Paris','1995-03-15'),
('550e8400-e29b-41d4-a716-446655440002','Dupont','Jean','jean@test.com',
 '$2b$10$OgLeRKxQNhoagnCW88DtYO5cQL96evCujayFRIWseaQp4hbpXttqq',
 '5 avenue des Lilas, 69001 Lyon','1990-07-22'),
('550e8400-e29b-41d4-a716-446655440003','Larsen','Erik','erik@test.com',
 '$2b$10$Qgzih5KzYCd/6zvUhuIL3euupIGtxy//1PMaasTBh0Eav/IBuv1We',
 '8 rue du Port, 13001 Marseille','1988-11-05'),
('550e8400-e29b-41d4-a716-446655440004','Martin','Marie','marie@test.com',
 '$2b$10$OgLeRKxQNhoagnCW88DtYO5cQL96evCujayFRIWseaQp4hbpXttqq',
 '3 boulevard Victor Hugo, 31000 Toulouse','1993-04-10'),
('550e8400-e29b-41d4-a716-446655440005','Bernard','Pierre','pierre@test.com',
 '$2b$10$OgLeRKxQNhoagnCW88DtYO5cQL96evCujayFRIWseaQp4hbpXttqq',
 '17 impasse des Roses, 44000 Nantes','1985-09-28');

-- ============================================================
-- 6. RÔLES
-- ============================================================
INSERT INTO user_roles (user_id, role) VALUES
('550e8400-e29b-41d4-a716-446655440001','ROLE_ADMIN'),
('550e8400-e29b-41d4-a716-446655440001','ROLE_USER'),
('550e8400-e29b-41d4-a716-446655440002','ROLE_USER'),
('550e8400-e29b-41d4-a716-446655440003','ROLE_USER'),
('550e8400-e29b-41d4-a716-446655440004','ROLE_USER'),
('550e8400-e29b-41d4-a716-446655440005','ROLE_USER');

-- ============================================================
-- 7. COMMANDES
-- ============================================================
INSERT INTO command (id, user_id, date_commande, date_expedition, date_livraison, status, total_price, adresse_livraison) VALUES
(1,'550e8400-e29b-41d4-a716-446655440001','2026-01-10 09:00:00','2026-01-12 08:00:00','2026-01-15 10:00:00','DELIVERED',86.48,'12 rue de Paris, 75001 Paris'),
(2,'550e8400-e29b-41d4-a716-446655440001','2026-02-20 14:30:00',NULL,NULL,'PENDING',39.70,'12 rue de Paris, 75001 Paris'),
(3,'550e8400-e29b-41d4-a716-446655440002','2026-03-05 11:15:00','2026-03-07 09:00:00',NULL,'SHIPPED',49.00,'5 avenue des Lilas, 69001 Lyon'),
(4,'550e8400-e29b-41d4-a716-446655440003','2026-03-18 16:45:00',NULL,NULL,'CANCELLED',27.00,'8 rue du Port, 13001 Marseille'),
(5,'550e8400-e29b-41d4-a716-446655440004','2026-04-02 10:00:00','2026-04-04 08:30:00','2026-04-07 14:00:00','DELIVERED',44.00,'3 boulevard Victor Hugo, 31000 Toulouse'),
(6,'550e8400-e29b-41d4-a716-446655440005','2026-04-15 09:30:00','2026-04-17 07:00:00',NULL,'SHIPPED',53.00,'17 impasse des Roses, 44000 Nantes'),
(7,'550e8400-e29b-41d4-a716-446655440002','2026-04-20 15:00:00','2026-04-22 09:00:00','2026-04-25 11:00:00','DELIVERED',48.00,'5 avenue des Lilas, 69001 Lyon'),
(8,'550e8400-e29b-41d4-a716-446655440004','2026-05-01 11:00:00',NULL,NULL,'PENDING',44.49,'3 boulevard Victor Hugo, 31000 Toulouse');

ALTER TABLE command AUTO_INCREMENT = 9;

-- ============================================================
-- 8. LIGNES DE COMMANDES
-- ============================================================
INSERT INTO command_item (id, command_id, livre_id, quantite, prix_unitaire) VALUES
(1,1,1,2,15.99),(2,1,5,1,24.50),(3,1,11,1,30.00),
(4,2,4,1,14.50),(5,2,7,1,14.20),(6,2,8,1,11.00),
(7,3,5,2,24.50),
(8,4,3,1,18.00),(9,4,19,1,9.00),
(10,5,12,1,15.00),(11,5,15,1,12.00),(12,5,9,2,9.50),
(13,6,13,1,16.80),(14,6,14,1,22.00),(15,6,7,1,14.20),
(16,7,11,1,30.00),(17,7,3,1,18.00),
(18,8,5,1,24.50),(19,8,6,1,19.99);

ALTER TABLE command_item AUTO_INCREMENT = 20;

-- ============================================================
-- 9. PANIERS
-- ============================================================
INSERT INTO panier (id, user_id) VALUES
(1,'550e8400-e29b-41d4-a716-446655440002'),
(2,'550e8400-e29b-41d4-a716-446655440003'),
(3,'550e8400-e29b-41d4-a716-446655440004'),
(4,'550e8400-e29b-41d4-a716-446655440005');

ALTER TABLE panier AUTO_INCREMENT = 5;

-- ============================================================
-- 10. ARTICLES PANIERS
-- ============================================================
INSERT INTO panier_item (id, panier_id, livre_id, quantite) VALUES
(1,1,12,1),(2,1,15,2),
(3,2,14,1),(4,2,13,1),
(5,3,1,1),(6,3,2,1),
(7,4,18,2),(8,4,17,1);

ALTER TABLE panier_item AUTO_INCREMENT = 9;