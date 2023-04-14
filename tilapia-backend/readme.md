# Tilapia Backend

### ⚠️ Warning
1. Do not put this in a production environment without an reverse proxy

## Design / Control Flow
### Database Access
A connected session could request session Database access, once the request has been received,
the server will create a temporary Database user with permission to access specific tables.

The username and password will be randomly generated, with IP strict to the forwarded IP
from the reverse proxy.

Once a connection to the session is closed, related database users will be deleted


## Client API

The client API is located in module `tilapia-communication`. It's also published into our
private maven repository.

For normal usage, please check the test of the backend, for the actual usage, 
please check the source of `tilapia-core` or `tilapia-bungee-core`