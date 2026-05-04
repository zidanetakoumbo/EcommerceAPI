INSERT INTO categorie (nom_cat) VALUES ('Science-Fiction'), ('Dystopie'), ('Classique'), ('Espace'), ('Philosophie'), ('Policier'), ('Fantastique'), ('Horreur'), ('Biographie'), ('Aventure');

INSERT INTO autheur (nom, prenom) VALUES 
('Orwell', 'George'), 
('Asimov', 'Isaac'), 
('Herbert', 'Frank'), 
('Barjavel', 'René'), 
('Christie', 'Agatha'), 
('Tolkien', 'J.R.R.'), 
('King', 'Stephen'), 
('Hugo', 'Victor'), 
('Verne', 'Jules'), 
('Camus', 'Albert');

INSERT INTO livre (titre, prix, autheur_id, resume, out_date, open_couverture, close_couverture) VALUES 
('1984', 15.99, 1, 'Big Brother vous regarde.', '1949-06-08', 'https://images.example.com/1984_front.jpg', 'https://images.example.com/1984_back.jpg'),
('La Ferme des animaux', 12.50, 1, 'Satire de la révolution russe.', '1945-08-17', 'https://images.example.com/ferme_front.jpg', 'https://images.example.com/ferme_back.jpg'),
('Fondation', 18.00, 2, 'La psychohistoire de Seldon.', '1951-06-01', 'https://images.example.com/fondation_front.jpg', 'https://images.example.com/fondation_back.jpg'),
('Les Robots', 14.50, 2, 'Les trois lois de la robotique.', '1950-12-02', 'https://images.example.com/robots_front.jpg', 'https://images.example.com/robots_back.jpg'),
('Dune', 24.50, 3, 'L''épice sur Arrakis.', '1965-08-01', 'https://images.example.com/dune_front.jpg', 'https://images.example.com/dune_back.jpg'),
('Le Messie de Dune', 19.99, 3, 'Le destin de Paul Atreides.', '1969-10-01', 'https://images.example.com/messie_dune_front.jpg', 'https://images.example.com/messie_dune_back.jpg'),
('La Nuit des temps', 14.20, 4, 'Un amour vieux de 900 000 ans.', '1968-04-01', 'https://images.example.com/nuit_temps_front.jpg', 'https://images.example.com/nuit_temps_back.jpg'),
('Ravage', 11.00, 4, 'Le monde sans électricité.', '1943-01-01', 'https://images.example.com/ravage_front.jpg', 'https://images.example.com/ravage_back.jpg'),
('Le Crime de l''Orient-Express', 9.50, 5, 'Hercule Poirot mène l''enquête.', '1934-01-01', 'https://images.example.com/orient_express_front.jpg', 'https://images.example.com/orient_express_back.jpg'),
('Ils étaient dix', 10.00, 5, 'Dix personnes sur une île.', '1939-11-06', 'https://images.example.com/dix_front.jpg', 'https://images.example.com/dix_back.jpg'),
('Le Seigneur des Anneaux', 30.00, 6, 'La quête de Frodon.', '1954-07-29', 'https://images.example.com/sda_front.jpg', 'https://images.example.com/sda_back.jpg'),
('Le Hobbit', 15.00, 6, 'L''aventure de Bilbon.', '1937-09-21', 'https://images.example.com/hobbit_front.jpg', 'https://images.example.com/hobbit_back.jpg'),
('Shining', 16.80, 7, 'L''hôtel Overlook.', '1977-01-28', 'https://images.example.com/shining_front.jpg', 'https://images.example.com/shining_back.jpg'),
('Ca', 22.00, 7, 'Le clown de Derry.', '1986-09-15', 'https://images.example.com/ca_front.jpg', 'https://images.example.com/ca_back.jpg'),
('Les Misérables', 12.00, 8, 'Jean Valjean et Javert.', '1862-01-01', 'https://images.example.com/miserables_front.jpg', 'https://images.example.com/miserables_back.jpg'),
('Notre-Dame de Paris', 11.50, 8, 'Quasimodo et Esmeralda.', '1831-01-01', 'https://images.example.com/notredame_front.jpg', 'https://images.example.com/notredame_back.jpg'),
('Vingt mille lieues sous les mers', 13.00, 9, 'Le capitaine Nemo.', '1870-01-01', 'https://images.example.com/20000lieues_front.jpg', 'https://images.example.com/20000lieues_back.jpg'),
('Le Tour du monde en 80 jours', 10.50, 9, 'Phileas Fogg.', '1872-01-01', 'https://images.example.com/80jours_front.jpg', 'https://images.example.com/80jours_back.jpg'),
('L''Etranger', 9.00, 10, 'Meursault face à l''absurde.', '1942-01-01', 'https://images.example.com/etranger_front.jpg', 'https://images.example.com/etranger_back.jpg'),
('La Peste', 10.00, 10, 'Oran sous l''épidémie.', '1947-06-10', 'https://images.example.com/peste_front.jpg', 'https://images.example.com/peste_back.jpg');
INSERT INTO livre_cat (livre_id, cat_id) VALUES 
(1, 1), (1, 2), 
(2, 3), 
(3, 1), (3, 4), 
(4, 1), 
(5, 1), (5, 4), (5, 5),
(6, 1), (6, 4),
(7, 1), (7, 3),
(8, 1), (8, 2),
(9, 6),
(10, 6),
(11, 7), (11, 10),
(12, 7), (12, 10),
(13, 8),
(14, 8),
(15, 3),
(16, 3),
(17, 10), (17, 4),
(18, 10),
(19, 3), (19, 5),
(20, 3), (20, 5);

INSERT INTO app_user (id, nom, prenom, email, password) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'Zidane', 'Zizou', 'admin@zizou.com', '$2a$10$2y6W1.pY6Y/F5xY9yW1.OunW/A.Y6yW1.pY6Y/F5xY9yW1.OunW/A'),
('a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6', 'Dupont', 'Jean', 'jean@test.com', '$2a$10$2y6W1.pY6Y/F5xY9yW1.OunW/A.Y6yW1.pY6Y/F5xY9yW1.OunW/A'),
('b2c3d4e5-f6g7-8h9i-0j1k-l2m3n4o5p6q7', 'Larsen', 'Erik', 'mod@test.com', '$2a$10$2y6W1.pY6Y/F5xY9yW1.OunW/A.Y6yW1.pY6Y/F5xY9yW1.OunW/A');

INSERT INTO app_user_roles (app_user_id, roles) VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'ADMIN'),
('550e8400-e29b-41d4-a716-446655440000', 'USER'),
('a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6', 'USER'),
('b2c3d4e5-f6g7-8h9i-0j1k-l2m3n4o5p6q7', 'MODERATOR'),
('b2c3d4e5-f6g7-8h9i-0j1k-l2m3n4o5p6q7', 'USER');