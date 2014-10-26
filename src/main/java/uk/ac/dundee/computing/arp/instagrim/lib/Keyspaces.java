package uk.ac.dundee.computing.arp.instagrim.lib;


import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    // TODO - Change user table to include salt
    
    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createkeyspace = "create keyspace if not exists instagrimarp  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreatePicTable = "CREATE TABLE if not exists instagrimarp.Pics ("
                    + " user varchar,"
                    + " picid uuid, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + " processedlength int,"
                    + " sepia blob,"
                    + " sepialength int,"
                    + " negative blob,"
                    + " negativelength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " PRIMARY KEY (picid)"
                    + ")";
            String Createuserpiclist = "CREATE TABLE if not exists instagrimarp.userpiclist (\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "pic_added timestamp,\n"
                    + "PRIMARY KEY (user,pic_added)\n" 
                    + ") WITH CLUSTERING ORDER BY (pic_added desc);";
            String CreateAddressType = "CREATE TYPE if not exists instagrimarp.address (\n"
                    + "      street text,\n"
                    + "      city text,\n"
                    + "      zip int\n"
                    + "  );";
            String CreateUserProfile = "CREATE TABLE if not exists instagrimarp.userprofiles (\n"
                    + "      login text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      salt text,\n"
                    + "      profilepic blob,\n"
                    + "      profilepiclength int,\n"
                    + "      profilepictype text,\n"
                    + "      first_name text,\n"
                    + "      last_name text,\n"
                    + "      email set<text>,\n"
                    + "      joined timestamp,\n"
                    + "      addresses  map<text, frozen <address>>\n"
                    + "  );";
            String CreateComments = "CREATE TABLE if not exists instagrimarp.comments(\n"
                    + "      picid uuid,\n"
                    + "      author varchar,\n"
                    + "      comment text,\n"
                    + "      writtenOn timestamp,\n"
                    + "      PRIMARY KEY (picid, writtenOn)\n"
                    + ") WITH CLUSTERING ORDER BY (writtenOn desc);";
            String CreateTags = "CREATE TABLE if not exists instagrimarp.tags(\n"
                    + "      tagid uuid,\n"
                    + "      tag varchar,\n"
                    + "      count counter,\n"
                    + "      PRIMARY KEY (tagid,tag)\n"
                    + ");";
            String CreateTagsToPic = "CREATE TABLE if not exists instagrimarp.tagpic(\n"
                    + "      tagid uuid,\n"
                    + "      picid uuid,\n"
                    + "      PRIMARY KEY (picid, tagid)\n"
                    + ");";
            /*String CreateFavourites = "CREATE TABLE if not exists instagrim.favourites(\n"
                    + "      user text,\n"
                    + "      picid uuid,\n"
                    + "      PRIMARY KEY (user, picid)\n"
                    + ");";*/
            
            Session session = c.connect();
            try {
                PreparedStatement statement = session
                        .prepare(createkeyspace);
                BoundStatement boundStatement = new BoundStatement(
                        statement);
                ResultSet rs = session
                        .execute(boundStatement);
                System.out.println("created instagrimarp ");
            } catch (Exception et) {
                System.out.println("Can't create instagrimarp " + et);
            }

            //now add some column families 
            System.out.println("" + CreatePicTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreatePicTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create pic table " + et);
            }
            System.out.println("" + Createuserpiclist);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(Createuserpiclist);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create user pic list table " + et);
            }
            System.out.println("" + CreateAddressType);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateAddressType);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address type " + et);
            }
            System.out.println("" + CreateUserProfile);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateUserProfile);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create Address Profile " + et);
            }
            System.out.println("" + CreateComments);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateComments);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create comments table " + et);
            }
            System.out.println("" + CreateTags);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateTags);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create tags table" + et);
            }
            System.out.println("" + CreateTagsToPic);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateTagsToPic);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create tags to picture table" + et);
            }
            
            session.close();

        } catch (Exception et) {
            System.out.println("Other keyspace or coulm definition error" + et);
        }

    }
}
