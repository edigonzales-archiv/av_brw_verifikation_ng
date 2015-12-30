#!/bin/bash

DBADMIN="stefan"
DBADMINPWD="ziegler12"
DBUSR="mspublic"
DBPWD="mspublic"
DBNAME="sogis_brw_verifikation"


# Create some database roles, create a database and install postgis extension
sudo -u postgres psql -d postgres -c "CREATE ROLE $DBADMIN CREATEDB LOGIN PASSWORD '$DBADMINPWD';"
sudo -u postgres psql -d postgres -c "CREATE ROLE $DBUSR LOGIN PASSWORD '$DBPWD';"
sudo -u postgres dropdb $DBNAME
sudo -u postgres createdb --owner $DBADMIN $DBNAME
sudo -u postgres psql -d $DBNAME -c "CREATE EXTENSION postgis;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON SCHEMA public TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "ALTER TABLE geometry_columns OWNER TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON geometry_columns TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON spatial_ref_sys TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON geography_columns TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON raster_columns TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT ALL ON raster_overviews TO $DBADMIN;"
sudo -u postgres psql -d $DBNAME -c "GRANT SELECT ON geometry_columns TO $DBUSR;"
sudo -u postgres psql -d $DBNAME -c "GRANT SELECT ON spatial_ref_sys TO $DBUSR;"
sudo -u postgres psql -d $DBNAME -c "GRANT SELECT ON geography_columns TO $DBUSR;"
sudo -u postgres psql -d $DBNAME -c "GRANT SELECT ON raster_columns TO $DBUSR;"
