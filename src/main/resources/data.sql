INSERT INTO keyword (keyword)
VALUES
    ('junior'),
    ('java'),
    ('remote'),
    ('python');

INSERT INTO project (description, location, url)
VALUES
    ('Beskrivning av konsultuppdrag', 'Hawkins', 'https://1'),
    ('Ännu en beskrivning av konsultuppdrag', 'Östersund', 'https://2'),
    ('En till beskrivning av konsultuppdrag', 'Derry', 'https://3');

INSERT INTO project_keyword (project_id, keyword_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (3, 3);