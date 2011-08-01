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
import net.sf.farrago.type.*;
import org.eigenbase.reltype.*;
import java.util.logging.*;
import net.sf.farrago.trace.*;

/**
 * This class ties up the relationship between
 * wrapper, server, and foreign table.
 * @author Kevin Secretan
 */
public class MedCouchDataServer extends MedAbstractDataServer {

  // These are options taken by the server and potentially by the foreign table.
  public static final String PROP_USERNAME = "USERNAME";
  public static final String DEFAULT_USERNAME = "";
  
  public static final String PROP_PASSWORD = "PASSWORD";
  public static final String DEFAULT_PASSWORD = "";

  public static final String PROP_URL = "URL";
  public static final String DEFAULT_URL = "http://localhost:5984";

  public static final String PROP_VIEW = "VIEW";
  
  public static final String PROP_VIEW_DEF = "VIEW_DEF";
  public static final String DEFAULT_VIEW_DEF = "";

  public static final String PROP_LIMIT = "LIMIT";
  public static final String DEFAULT_LIMIT = "";

  public static final String PROP_REDUCE = "REDUCE";
  public static final boolean DEFAULT_REDUCE = true;

  public static final String PROP_GROUP_LEVEL = "GROUP_LEVEL";
  public static final String DEFAULT_GROUP_LEVEL = "EXACT";

  public static final String PROP_OUTPUT_JSON = "OUTPUT_JSON";
  public static final boolean DEFAULT_OUTPUT_JSON = false;

  public static final String PROP_UDX_SPECIFIC_NAME = "UDX_SPECIFIC_NAME";
  public static final String DEFAULT_UDX_SPECIFIC_NAME
    = "LOCALDB.SYS_COUCHDB.WRAPPER_UDX"; // must be created

  private MedAbstractDataWrapper wrapper;
  // server uses these values
  private String un;
  private String pw;
  private String url;
  private String limit;
  private boolean reduce;
  private String groupLevel;
  private boolean outputJson;

  private static final Logger tracer
    = FarragoTrace.getClassTracer(MedCouchDataServer.class);

  public MedCouchDataServer(
      MedAbstractDataWrapper wrapper,
      String serverMofId,
      Properties props)
  {
    super(serverMofId, props);
    this.wrapper = wrapper;
  }

  /**
   * Called on server creation, we can require certain server properties here or
   * just set some server defaults.
   */
  public void initialize() throws SQLException {
    Properties props = getProperties();
    un = props.getProperty(PROP_USERNAME, DEFAULT_USERNAME);
    pw = props.getProperty(PROP_PASSWORD, DEFAULT_PASSWORD);
    url = props.getProperty(PROP_URL, DEFAULT_URL);
    limit = props.getProperty(PROP_LIMIT, DEFAULT_LIMIT);
    reduce = getBooleanProperty(props, PROP_REDUCE, DEFAULT_REDUCE);
    groupLevel = props.getProperty(PROP_GROUP_LEVEL, DEFAULT_GROUP_LEVEL);
    outputJson = getBooleanProperty(props, PROP_OUTPUT_JSON, DEFAULT_OUTPUT_JSON);
  }

  // implment FarragoMedDataServer
  /**
   * Called on creation of a foreign table, tableProps are passed F.T.
   * specific properties and rowType is the expected column types that F.T.
   * expects.
   */
  public FarragoMedColumnSet newColumnSet(
      String[] localName,
      Properties tableProps,
      FarragoTypeFactory typeFactory,
      RelDataType rowType,
      Map<String, Properties> columnPropMap) throws SQLException
  {
    //tracer.info(rowType.toString());
    String udxSpecificName = getProperties().getProperty(
        PROP_UDX_SPECIFIC_NAME,
        DEFAULT_UDX_SPECIFIC_NAME);

    // override any server props:
    String myUn = tableProps.getProperty(PROP_USERNAME, un);
    String myPw = tableProps.getProperty(PROP_PASSWORD, pw);
    String myUrl = tableProps.getProperty(PROP_URL, url);
    String myLimit = tableProps.getProperty(PROP_LIMIT, limit);
    boolean myReduce = getBooleanProperty(tableProps, PROP_REDUCE, reduce);
    String myGroupLevel = tableProps.getProperty(PROP_GROUP_LEVEL, groupLevel);
    boolean myOutputJson = getBooleanProperty(tableProps, PROP_OUTPUT_JSON, outputJson);

    requireProperty(tableProps, PROP_VIEW);
    String view = tableProps.getProperty(PROP_VIEW);

    // we only support view_def on server definitions.
    // since we can't easily rewrite the foreign table storage.
    String viewDef = getProperties().getProperty(PROP_VIEW_DEF, DEFAULT_VIEW_DEF);
    if (!viewDef.equals("")) {
      // don't save it in the final server data.
      getProperties().remove(PROP_VIEW_DEF);
    }

    return new MedCouchColumnSet(
        this,
        localName,
        rowType,
        myUn,
        myPw,
        myUrl,
        view,
        viewDef,
        myLimit,
        myReduce,
        myGroupLevel,
        myOutputJson,
        udxSpecificName);
  }

  // someone might want this
  public MedAbstractDataWrapper getWrapper() {
    return wrapper;
  }

}
