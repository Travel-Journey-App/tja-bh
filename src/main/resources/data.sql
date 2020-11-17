insert into roles (id, name)
values (1, 'ROLE_USER'),
       (0, 'ROLE_ADMIN')
on conflict do nothing;