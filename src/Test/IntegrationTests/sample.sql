
-- Inserts some sample data into the database.
-- Included memos contain:
-- One thread (3) with a single note
-- One thread (1) with two notes
-- One thread (2) with two notes, newer one being marked as deleted.
insert into memos(title, content, version, deleted, created, modified, threadid) values
('Groceries', 'Eggs, apples, donuts.', 1, 0, '2017-01-01', '2017-01-01', 1),
('ToDo this week', 'Haircut, road trip, wash the car.', 1, 0, '2017-01-02', '2017-02-02', 2),
('Groceries', 'Eggs, apples, carrots.', 2, 0, '2017-01-01', '2017-01-02', 1),
('Homework', 'MySQL, Gradle, Integration testing.', 1, 0, '2017-01-10', '2017-01-10', 3),
('ToDo this and next week', 'Haircut, road trip to the sea, wash the car and the dog.', 2, 1, '2017-01-02', '2017-02-03', 2)