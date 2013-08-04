-- Create account types
insert into MEVEO_ACCOUNT_TYPE (id, version, account_type, code, name_type, parent_account_type_id) values(1, 0, 'serviceProvider', 'manaty','business',null);
insert into MEVEO_ACCOUNT_TYPE (id, version, account_type, code, name_type, parent_account_type_id) values(2, 0, 'customer', 'meveo','business',1);

DROP SEQUENCE MEVEO_ACCOUNT_TYPE_SEQ;
CREATE SEQUENCE MEVEO_ACCOUNT_TYPE_SEQ start with 3 increment by 1;

-- Create roles and permissions
insert into MEVEO_ROLE (id, role_name, role_description) values (1,'admin','Administrateur');
insert into MEVEO_ROLE (id, role_name, role_description) values (2,'supervisor','Superviseur');
insert into MEVEO_ROLE (id, role_name, role_description) values (3,'operator','Operator');
insert into MEVEO_ROLE (id, role_name, role_description)values  (4,'collaborator','Collaborator');

DROP SEQUENCE MEVEO_ROLE_SEQ;
CREATE SEQUENCE MEVEO_ROLE_SEQ start with 5 increment by 1;

insert into MEVEO_ROLE_ACCOUNT_TYPE (role_id, account_type_id) values (2,1);
insert into MEVEO_ROLE_ACCOUNT_TYPE (role_id, account_type_id) values (3,1);
insert into MEVEO_ROLE_ACCOUNT_TYPE (role_id, account_type_id) values (4,2);

insert into meveo_role_account_type_manage (role_id, account_type_id) values (1,1);
insert into meveo_role_account_type_manage (role_id, account_type_id) values (1,2);

insert into MEVEO_PERMISSION (id, name, resource, permission) values (1,'userManager','user','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (2,'businessAccountManager','businessAccount','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (3,'jobManager','job','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (4,'interactionManager','interaction','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (5,'customerManager','customer','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (6,'questionManager','question','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (7,'taskManager','task','manage');
insert into MEVEO_PERMISSION (id, name, resource, permission) values (8,'customerViewer','customer','view');

DROP SEQUENCE MEVEO_PERMISSION_SEQ;
CREATE SEQUENCE MEVEO_PERMISSION_SEQ start with 9 increment by 1;

insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,1);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,2);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,3);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,4);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,5);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,6);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(1,7);

insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(2,3);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(2,4);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(2,5);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(2,6);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(2,7);

insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(3,3);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(3,4);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(3,5);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(3,6);
insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(3,7);

insert into MEVEO_ROLE_PERMISSION(role_id,permission_id) values(4,8);

-- Create business accounts
insert into MEVEO_ACCOUNT (id,version,disabled,updated,buss_name,code,name_for_search,account_type_id,created) values(1,0,false,now(),'Manaty','MANATY','MANATY',1,'2013-07-10');
insert into MEVEO_ACCOUNT (id,version,disabled,updated,buss_name,code,name_for_search,account_type_id,created) values(2,0,false,now(),'MEVEO','MEVEO','MEVEO',2,'2013-07-10');

DROP SEQUENCE MEVEO_ACCOUNT_SEQ;
CREATE SEQUENCE MEVEO_ACCOUNT_SEQ start with 3 increment by 1;

-- Create users
insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username,email,skype,mobile,phone,created) values (1,0,now(),false,now(),'System','Admin','d033e22ae348aeb5660fc2140aec35850c4da997','admin','edward.legaspi@manaty.net','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,1 from meveo_user where username='admin';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (2,0,now(),false,now(),'manaty','manager','d033e22ae348aeb5660fc2140aec35850c4da997','manaty',(select id from meveo_account where name_for_search='MANATY'),'edward.legaspi@manaty.net','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,2 from meveo_user where username='manaty';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (3,0,now(),false,now(),'meveo','manager','d033e22ae348aeb5660fc2140aec35850c4da997','meveo',(select id from meveo_account where name_for_search='MEVEO'),'edward.legaspi@manaty.net','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,3 from meveo_user where username='meveo';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (4,0,now(),false,now(),'kalidad','manager','d033e22ae348aeb5660fc2140aec35850c4da997','kalidad',(select id from meveo_account where name_for_search='KALIDAD'),'edward.legaspi@manaty.net','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,4 from meveo_user where username='kalidad';

DROP SEQUENCE MEVEO_USER_SEQ;
CREATE SEQUENCE MEVEO_USER_SEQ start with 5 increment by 1;

-- Set principal contact for accounts
update MEVEO_ACCOUNT set principal_contact_id= (select id from meveo_user where username='manaty') where name_for_search='MANATY';
update MEVEO_ACCOUNT set principal_contact_id= (select id from meveo_user where username='meveo') where name_for_search='MEVEO';

INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (1, 'M', false);
INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (2, 'MLLE', false);
INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (3, 'MME', false);

DROP SEQUENCE MEVEO_TITLE_SEQ;
CREATE SEQUENCE MEVEO_TITLE_SEQ start with 4 increment by 1;