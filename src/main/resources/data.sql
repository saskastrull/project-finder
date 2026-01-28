INSERT INTO keyword (keyword)
VALUES
    ('junior'),
    ('java'),
    ('remote'),
    ('python');

INSERT INTO project (description, location, hours, url)
VALUES
    ('Beskrivning av konsultuppdrag', 'Hawkins', 50, 'https://1'),
    ('Ännu en beskrivning av konsultuppdrag', 'Östersund', 50, 'https://2'),
    ('En till beskrivning av konsultuppdrag', 'Derry', 100, 'https://3');

INSERT INTO project_keyword (project_id, keyword_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (3, 3);