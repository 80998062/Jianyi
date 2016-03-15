package com.sinyuk.jianyimaterial.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.sinyuk.jianyimaterial.model.School;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SCHOOL".
*/
public class SchoolDao extends AbstractDao<School, String> {

    public static final String TABLENAME = "SCHOOL";

    /**
     * Properties of entity School.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Coord = new Property(2, String.class, "coord", false, "COORD");
        public final static Property X = new Property(3, String.class, "x", false, "X");
        public final static Property Y = new Property(4, String.class, "y", false, "Y");
    };


    public SchoolDao(DaoConfig config) {
        super(config);
    }
    
    public SchoolDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SCHOOL\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"COORD\" TEXT," + // 2: coord
                "\"X\" TEXT," + // 3: x
                "\"Y\" TEXT);"); // 4: y
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SCHOOL\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, School entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
        stmt.bindString(2, entity.getName());
 
        String coord = entity.getCoord();
        if (coord != null) {
            stmt.bindString(3, coord);
        }
 
        String x = entity.getX();
        if (x != null) {
            stmt.bindString(4, x);
        }
 
        String y = entity.getY();
        if (y != null) {
            stmt.bindString(5, y);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public School readEntity(Cursor cursor, int offset) {
        School entity = new School( //
            cursor.getString(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // coord
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // x
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // y
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, School entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setCoord(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setX(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setY(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(School entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(School entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
