ALTER TABLE boughtlist DROP CONSTRAINT boughtlist_pk;
ALTER TABLE productlist DROP CONSTRAINT productlist_pk;
ALTER TABLE boughtlist RENAME TO boughtitem;
ALTER TABLE productlist RENAME TO productitem;
ALTER TABLE boughtitem RENAME COLUMN boughtlistid TO boughtitemid; 
ALTER TABLE productitem RENAME COLUMN productlistid TO productitemid;
ALTER TABLE boughtitem ADD CONSTRAINT boughtitem_pk PRIMARY KEY(boughtitemid); 
ALTER TABLE productitem ADD CONSTRAINT productitem_pk PRIMARY KEY(productitemid); 