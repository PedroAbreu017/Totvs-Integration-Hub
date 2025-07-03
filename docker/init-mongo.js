db = db.getSiblingDB('admin');
db.createUser({
  user: 'admin',
  pwd: 'totvs123456789',
  roles: [ { role: 'root', db: 'admin' } ]
});

db = db.getSiblingDB('totvs_integration');
db.createUser({
  user: 'admin',
  pwd: 'totvs123456789',
  roles: [ { role: 'readWrite', db: 'totvs_integration' } ]
});