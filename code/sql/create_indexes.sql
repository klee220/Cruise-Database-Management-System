CREATE INDEX customer_id
ON Customer
USING BTREE
(id);

CREATE INDEX cruise_cnum
ON Cruise
USING BTREE
(cnum);

CREATE INDEX cruise_cost
ON Cruise
USING BTREE
(cost);

CREATE INDEX ship_id
ON Ship
USING BTREE
(id);
