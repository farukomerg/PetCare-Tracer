SELECT
    p.pet_name,
    u.full_name AS owner_name,
    p.species,
    p.breed
FROM pets p
JOIN users u ON p.user_id = u.user_id;