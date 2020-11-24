insert into gateway(serial_number, name, ip4_address)
values('A1', 'portal', '168.123.159.1');

insert into gateway(serial_number, name, ip4_address)
values('A2', 'portal2', '168.123.159.2');

insert into gateway(serial_number, name, ip4_address)
values('A3', 'half life 2', '168.123.159.3');

--------------------------------------------------------------------
insert into peripheral_device(uid, vendor, date_created, gateway_serial)
values('U1', 'Valve', CURDATE(), 'A1');

insert into peripheral_device(uid, vendor, date_created, gateway_serial)
values('U2', 'Roit', CURDATE(), 'A2');

insert into peripheral_device(uid, vendor, date_created, gateway_serial)
values('U3', 'Billzard', CURDATE(), 'A3');