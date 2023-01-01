CREATE TABLE KHL_CLUBS (club_name varchar, club_id varchar, conference varchar, city varchar);

BEGIN TRANSACTION;
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('СКА', 'ska', 'west', 'Санкт-Петербург');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('ЦСКА', 'cska', 'west', 'Москва');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Торпедо', 'torpedo', 'west', 'Нижний Новгород');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Локомотив', 'lokomotiv', 'west', 'Ярославль');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Динамо', 'dynamo_msk', 'west', 'Москва');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Динамо', 'dinamo_mn', 'west', 'Минск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Витязь', 'vityaz', 'west', 'Подольск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('ХК Сочи', 'hc_sochi', 'west', 'Сочи');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Спартак', 'spartak', 'west', 'Москва');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Северсталь', 'severstal', 'west', 'Череповец');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Куньлунь', 'kunlun', 'west', 'Пекин');
END TRANSACTION;

BEGIN TRANSACTION;
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Металлург', 'metallurg_mg', 'east', 'Магнитогорск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Авангард', 'avangard', 'east', 'Омск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Ак Барс', 'ak_bars', 'east', 'Казань');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Салават Юлаев', 'salavat_yulaev', 'east', 'Уфа');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Трактор', 'traktor', 'east', 'Челябинск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Адмирал', 'admiral', 'east', 'Владивосток');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Нефтехимик', 'neftekhimik', 'east', 'Нижнекамск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Сибирь', 'sibir', 'east', 'Новосибирск');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Барыс', 'barys', 'east', 'Астана');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Автомобилист', 'avtomobilist', 'east', 'Екатеринбург');
INSERT INTO KHL_CLUBS (club_name, club_id, conference, city) values ('Амур', 'amur', 'east', 'Хабаровск');
END TRANSACTION;