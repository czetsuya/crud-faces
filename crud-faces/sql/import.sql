-- Create account types
insert into MEVEO_ACCOUNT_TYPE (id, version, account_type, code, name_type, parent_account_type_id) values(1, 0, 'serviceProvider', 'numen','business',null);
insert into MEVEO_ACCOUNT_TYPE (id, version, account_type, code, name_type, parent_account_type_id) values(2, 0, 'customer', 'ancv','business',1);

DROP SEQUENCE MEVEO_ACCOUNT_TYPE_SEQ;
CREATE SEQUENCE MEVEO_ACCOUNT_TYPE_SEQ start with 3 increment by 1;

-- Create roles and permissions
insert into MEVEO_ROLE (id, role_name, role_description) values (1,'admin','Administrateur');
insert into MEVEO_ROLE (id, role_name, role_description) values (2,'supervisor','Superviseur');
insert into MEVEO_ROLE (id, role_name, role_description) values (3,'operator','Operateur');
insert into MEVEO_ROLE (id, role_name, role_description)values  (4,'collaborator','Collaborateur');

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
insert into MEVEO_ACCOUNT (id,version,disabled,updated,buss_name,code,name_for_search,account_type_id,created) values(1,0,false,now(),'Numen','NUMEN','NUMEN',1,'2013-07-10');
insert into MEVEO_ACCOUNT (id,version,disabled,updated,buss_name,code,name_for_search,account_type_id,created) values(2,0,false,now(),'ANCV','ANCV','ANCV',2,'2013-07-10');

DROP SEQUENCE MEVEO_ACCOUNT_SEQ;
CREATE SEQUENCE MEVEO_ACCOUNT_SEQ start with 3 increment by 1;

-- Create users
insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username,email,skype,mobile,phone,created) values (1,0,now(),false,now(),'System','Admin','d033e22ae348aeb5660fc2140aec35850c4da997','admin','test@test.com','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,1 from meveo_user where username='admin';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (2,0,now(),false,now(),'Numen','manager','1d764473da7ad824862b1b3343a76719f82e01f7','numen',(select id from meveo_account where name_for_search='NUMEN'),'test@test.com','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,2 from meveo_user where username='numen';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (3,0,now(),false,now(),'ancv','manager','cf080a4213bc4bbfcb99f9a0da75154d44a948fc','ancv',(select id from meveo_account where name_for_search='ANCV'),'ancv@test.com','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,3 from meveo_user where username='ancv';

insert into MEVEO_USER (id, version,updated,disabled,last_password_modification,first_name, last_name, password, username, account_id,email,skype,mobile,phone,created) values (4,0,now(),false,now(),'ancv1','manager','cf080a4213bc4bbfcb99f9a0da75154d44a948fc','ancv1',(select id from meveo_account where name_for_search='ANCV'),'ancv1@test.com','','','','2013-07-10');
insert into MEVEO_USER_ROLE(user_id, role_id) select id,4 from meveo_user where username='ancv1';

DROP SEQUENCE MEVEO_USER_SEQ;
CREATE SEQUENCE MEVEO_USER_SEQ start with 5 increment by 1;

-- Set principal contact for accounts
update MEVEO_ACCOUNT set principal_contact_id= (select id from meveo_user where username='numen') where name_for_search='NUMEN';
update MEVEO_ACCOUNT set principal_contact_id= (select id from meveo_user where username='ancv') where name_for_search='ANCV';

-- Create mail templates
INSERT INTO CONF_MAIL(id, version, archive, mail_code, mail_from, mail_subject, send_copie, send_copie_to, tpl_content, tpl_mail_format) VALUES (1, 0, false, 'USER_ACTIVATION', 'no_reply@numen.fr', 'Bienvenue sur le Portail ANCV-SEV', false, '', 'Bonjour,<br/><br/>Vous vous &ecirc;tes enregistr&eacute; en utilisant les informations suivantes : <br/><br/>Identifiant : $user.username$<br/>Mot de passe : $user.newPassword$<br/>Pour acc&eacute;der au portail : <a href="$loginUrl$">Se connecter</a><br/><br/>Merci, L''&eacute;quipe ANCV S&eacute;niors en vacances', 'html');
INSERT INTO CONF_MAIL(id, version, archive, mail_code, mail_from, mail_subject, send_copie, send_copie_to, tpl_content, tpl_mail_format) VALUES (2, 0, false, 'USER_PSWD_REMINDER', 'no_reply@numen.fr', 'Perte de mot de passe sur Portail Expert', false, '','Bonjour,<br/><br/>Nous avons reçu une demande de r&eacute;initialisation du mot de passe associ&eacute; &agrave; votre compte utilisateur.<br/><br/> Le nouveau mot de passe temporaire est : <b>$newPassword$</b><br/><br/>Merci de vous <a href="$loginUrl$">connecter</a> avec ce mot de passe temporaire sur le Portail Expert afin de le mettre à jour.<br/><br/><br/><br/>Merci, L''&eacute;quipe ANCV S&eacute;niors en vacances', 'html');

DROP SEQUENCE CONF_EMAIL_SEQ;
CREATE SEQUENCE CONF_EMAIL_SEQ start with 3 increment by 1;

INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (1, 'M', false);
INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (2, 'MLLE', false);
INSERT INTO MEVEO_TITLE (id, code, is_company) VAlUES (3, 'MME', false);

DROP SEQUENCE MEVEO_TITLE_SEQ;
CREATE SEQUENCE MEVEO_TITLE_SEQ start with 4 increment by 1;

INSERT INTO TYPE_VOIE (code) VALUES ('MAIN');

DELETE FROM crm_SUBSCRIPTION_BP_QUESTION;
DELETE FROM CRM_QUESTION;
DELETE FROM CRM_TASK_ALLOWED_ROLE;
DELETE FROM CRM_TASK;
DELETE FROM CRM_BUSINESS_PROCESS;
DELETE FROM CRM_INTERACTION;
DELETE FROM CRM_CUSTOMER;
DELETE FROM CRM_SHARED_ENTITY;

INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (1, 'CUST_1', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (2, 'CUST_2', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (3, 'CUST_3', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (4, 'CUST_4', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (5, 'CUST_5', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (6, 'CUST_6', current_date, 0);
INSERT INTO CRM_SHARED_ENTITY (id, ext_ref, created, version) VALUES (7, 'CUST_7', current_date, 0);

DROP SEQUENCE CRM_SHARED_ENTITY_SEQ;
CREATE SEQUENCE CRM_SHARED_ENTITY_SEQ start with 8 increment by 1;

INSERT INTO CRM_CUSTOMER (id, last_name, first_name, email, helper, birth_date, title, address_1, address_zipcode, address_city) VALUES (1,'Hugo', 'Victor', 'test1@ancv.com', false, '2013-07-19', 1, 'address1', '12345', 'city');
INSERT INTO CRM_CUSTOMER (id, last_name, first_name, email, helper, birth_date, title, address_1, address_zipcode, address_city) VALUES (2, 'Shakespeare', 'William', 'test2@ancv.com', true, '2013-07-19', 1, 'address1', '12345', 'city');
INSERT INTO CRM_CUSTOMER (id, last_name, first_name, email, helper, birth_date, title, address_1, address_zipcode, address_city) VALUES (3, 'Wordsworth', 'William', 'test3@ancv.com', false, '2013-07-19', 1, 'address1', '12345', 'city');
INSERT INTO CRM_CUSTOMER (id, last_name, first_name, email, helper, birth_date, title, address_1, address_zipcode, address_city) VALUES (4, 'Keats', 'John', 'test4@ancv.com', false, '2013-07-19', 1, 'address1', '12345', 'city');
INSERT INTO CRM_CUSTOMER (id, last_name, first_name, email, accompany_id, helper, birth_date, title, address_1, address_zipcode, address_city) VALUES (5, 'Tennesson', 'Alfred', 'test5@ancv.com', (SELECT id FROM crm_customer where email='test2@ancv.com'), false, '2013-07-19', 1, 'address1', '12345', 'city');
UPDATE crm_customer SET helper=false, retired=false, handicapped=false, dependent=false;

INSERT INTO CRM_INTERACTION (id, customer_id, date, interaction_direction) VALUES (6, 1, current_date, 'IN');
INSERT INTO CRM_INTERACTION (id, customer_id, date, interaction_direction) VALUES (7, 2, current_date, 'IN');

INSERT INTO CRM_BUSINESS_PROCESS (id, dtype, version, customer_id, interaction_id, role_id) VALUES (1, 'DOCUMENT', 0, 1, 6, 1);
INSERT INTO CRM_BUSINESS_PROCESS (id, dtype, version, customer_id, interaction_id, role_id) VALUES (2, 'DOCUMENT', 0, 1, 7, 2);
INSERT INTO CRM_BUSINESS_PROCESS (id, dtype, version, customer_id, interaction_id, role_id) VALUES (3, 'DOCUMENT', 0, 1, 6, 3);
INSERT INTO CRM_BUSINESS_PROCESS (id, dtype, version, customer_id, interaction_id, role_id) VALUES (4, 'DOCUMENT', 0, 1, 7, 4);

DROP SEQUENCE CRM_BUSINESS_PROCESS_SEQ;
CREATE SEQUENCE CRM_BUSINESS_PROCESS_SEQ start with 5 increment by 1;

INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (1, 0, current_date, 1, 'NEW_NOT_CREATOR', 'NEW', current_date, 3, 3, 6, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (2, 0, current_date, 3, 'NEW_CREATOR', 'NEW', current_date, 3, 3, 7, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (3, 0, current_date, 1, 'OPEN_1', 'OPEN', current_date, 1, 1, 6, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (4, 0, current_date, 2, 'OPEN_2', 'OPEN', current_date, 2, 2, 7, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (5, 0, current_date, 2, 'DONE', 'DONE', current_date, 2, 2, 6, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (6, 0, current_date, 3, 'DUPLICATE', 'DUPLICATE', current_date, 3, 3, 6, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (7, 0, current_date, 1, 'INVALID', 'INVALID', current_date, 1, 1, 7, 'hello world');
INSERT INTO CRM_TASK (id, version, created, creator_id, name, status, target_date, current_owner, current_target_role, origin_interaction, description) VALUES (8, 0, current_date, 3, 'CLOSED', 'CLOSED', current_date, 3, 3, 7, 'hello world');

DROP SEQUENCE CRM_TASK_SEQ;
CREATE SEQUENCE CRM_TASK_SEQ start with 9 increment by 1;

INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (1, 2);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (1, 3);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (2, 2);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (2, 3);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (3, 2);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (3, 3);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (4, 2);
INSERT INTO CRM_TASK_ALLOWED_ROLE (business_process_id, role_id) VALUES (4, 3);

INSERT INTO CRM_QUESTION (id, version, disabled, question, sorting_order, process) VALUES (1, 0, false, 'Proof of identity of the principal senior valid', 1, 'SubscriptionBP');
INSERT INTO CRM_QUESTION (id, version, disabled, question, sorting_order, process) VALUES (2, 0, false, 'Presence of the tax sheet', 2, 'SubscriptionBP');
INSERT INTO CRM_QUESTION (id, version, disabled, question, sorting_order, process, parent_id) VALUES (3, 0, false, 'Same name on the identification card and tax', 1, 'SubscriptionBP', 2);

DROP SEQUENCE CRM_QUESTION_SEQ;
CREATE SEQUENCE CRM_QUESTION_SEQ start with 4 increment by 1;