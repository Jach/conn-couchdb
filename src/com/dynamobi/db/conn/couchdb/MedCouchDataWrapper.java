/*
Dynamo CouchDB Connector is a plugin for browsing CouchDB views in LucidDB.
Copyright (C) 2011 Dynamo Business Intelligence Corporation

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option)
any later version approved by Dynamo Business Intelligence Corporation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package com.dynamobi.db.conn.couchdb;

import java.util.*;
import java.sql.*;

import net.sf.farrago.namespace.*;
import net.sf.farrago.namespace.impl.*;
import com.dynamobi.db.conn.couchdb.resource.*;

/**
 * Simple wrapper class for the creation of a foreign table wrapper, nothing
 * special.
 * @author Kevin Secretan
 */
public class MedCouchDataWrapper extends MedAbstractDataWrapper {

  public MedCouchDataWrapper() { }

  public String getSuggestedName() {
    return "CouchDB_FOREIGN_DATA_WRAPPER";
  }

  public String getDescription(Locale locale) {
    return "Foreign data wrapper for CouchDB";
  }

  // Implement FarragoMedDataWrapper:
  /**
   * This is responsible for binding an instance of this wrapper to some
   * foreign server.
   * @param serverMofId - the server's ID in the repo.
   * @param props - properties of the server
   * @return new server with this wrapper
   */
  public FarragoMedDataServer newServer(String serverMofId, Properties props)
    throws SQLException {
    MedCouchDataServer server = new MedCouchDataServer(this,serverMofId, props);
    try {
      server.initialize();
      return server;
    } catch (SQLException e) {
      server.closeAllocation();
      throw e;
    }
  }

  // implement FarragoMedDataWrapper
  public DriverPropertyInfo [] getServerPropertyInfo(
      Locale locale,
      Properties wrapperProps,
      Properties serverProps)
  {
    MedPropertyInfoMap infoMap =
      new MedPropertyInfoMap(
          MedCouchResource.instance(),
          "MedCouchDb",
          serverProps);
    infoMap.addPropInfo(MedCouchDataServer.PROP_USERNAME, false);
    infoMap.addPropInfo(MedCouchDataServer.PROP_PASSWORD, false);
    infoMap.addPropInfo(
        MedCouchDataServer.PROP_URL,
        false,
        new String[] {MedCouchDataServer.DEFAULT_URL});
    infoMap.addPropInfo(MedCouchDataServer.PROP_VIEW, false);
    infoMap.addPropInfo(MedCouchDataServer.PROP_VIEW_DEF, false);
    infoMap.addPropInfo(MedCouchDataServer.PROP_LIMIT, false);
    infoMap.addPropInfo(MedCouchDataServer.PROP_REDUCE, false,
        new String[] {new Boolean(
          MedCouchDataServer.DEFAULT_REDUCE).toString()});
    infoMap.addPropInfo(MedCouchDataServer.PROP_GROUP_LEVEL, false,
        new String[] {MedCouchDataServer.DEFAULT_GROUP_LEVEL});
    infoMap.addPropInfo(MedCouchDataServer.PROP_OUTPUT_JSON, false,
        new String[] {new Boolean(
          MedCouchDataServer.DEFAULT_OUTPUT_JSON).toString()});

    return infoMap.toArray();
  }
}
