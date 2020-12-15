insert into roles (id, name)
values (1, 'ROLE_USER'),
       (0, 'ROLE_ADMIN')
on conflict do nothing;

alter table if exists event_activity
    alter column description type varchar(2000);