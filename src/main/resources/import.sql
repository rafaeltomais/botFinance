insert into usuario (id, login, user_name, password, should_notificate) values (1, 'rafaeltomais', 'rafael', '123', false);
insert into usuario (id, login, user_name, password, should_notificate) values (2, 'lidiannetomais', 'lidianne', '123', false);
insert into usuario (id, login, user_name, password, should_notificate) values (3, 'elvistomais', 'elvis', '123', false);

insert into conta (due_day, description, due_value, payed, due_open, overdue, usuario_id) values (5, 'Mens. Seminario', '150', true, false, false, 1);
insert into conta (due_day, description, due_value, payed, due_open, overdue, usuario_id) values (10, 'Energia', '120', false, true, false, 1);
insert into conta (due_day, description, due_value, payed, due_open, overdue, usuario_id) values (20, 'Fatura Santander', '1000', false, true, false, 1);
insert into conta (due_day, description, due_value, payed, due_open, overdue, usuario_id) values (1, 'Shein', '50', true, false, false, 2);