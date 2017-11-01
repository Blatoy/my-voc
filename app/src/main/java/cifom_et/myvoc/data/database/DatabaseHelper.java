package cifom_et.myvoc.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Loïck Jeanneret
 * Based on the official documentation
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "MyVoc.db";
    private SQLiteDatabase db;

    /**
     * @param ctx -
     */
    public DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        // Get the database
        this.db = this.getWritableDatabase();
    }

    /**
     * Called when the database is created
     *
     * @param db -
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * Called when the version of the database change
     *
     * @param db         -
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // IMPROVE: A version check may be perform here
    }

    /**
     * Insert a result into the database
     *
     * @param table  The table name
     * @param values The key->value array to insert
     * @return the id of the inserted data
     */
    public long insert(String table, ContentValues values) {
        return this.db.insert(table, null, values);
    }

    /**
     * Update one ore multiple rows in the database
     *
     * @param table       The table to update
     * @param values      The values to update
     * @param whereClause The where clause to update
     * @return The number of row affected
     */
    // Update
    public int update(String table, ContentValues values, String whereClause) {
        return this.update(table, values, whereClause, null);
    }

    /**
     * Update one ore multiple rows in the database
     *
     * @param table       The table to update
     * @param values      The values to update
     * @param whereClause The where clause to update
     * @param whereArgs   The args if a prepared query was made
     * @return The number of row affected
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return this.db.update(table, values, whereClause, whereArgs);
    }

    /**
     * Delete on more multiple rows in the database
     *
     * @param table       The table to delete
     * @param whereClause The where clause to delete
     * @param whereArgs   The args if a prepared query was made
     * @return The number of row deleted
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        return this.db.delete(table, whereClause, whereArgs);
    }

    /**
     * Truncate the table
     *
     * @param table The table to truncate
     * @return The number of row deleted
     */
    public int truncate(String table) {
        return this.db.delete(table, null, null);
    }

    /**
     * Get results from the database
     *
     * @param table         The name of the table
     * @param columns       The list of columns to get
     * @param selection     The where cause
     * @param selectionArgs The args if a prepared query was made
     * @param orderBy       How to sort the results
     * @return A cursor with the data
     */
    public Cursor getResults(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return this.getResults(table, columns, selection, selectionArgs, null, null, orderBy, null);
    }

    /**
     * Get results from the database
     *
     * @param table         The name of the table
     * @param columns       The list of columns to get
     * @param selection     The where cause
     * @param selectionArgs The args if a prepared query was made
     * @param groupBy       The group by clause
     * @param having        The having clause
     * @param orderBy       How to sort the results
     * @param limit         The limit clause
     * @return A cursor with the data
     */
    public Cursor getResults(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    // Currently not in use, may be useful for the future
    /*public Cursor getResults(String table) {
        return db.query(table, null, null, null, null, null, null);
    }

    public Cursor getResults(String table, String[] columns) {
        return this.getResults(table, columns, null, null, null, null, null, null);
    }

    public Cursor getResults(String table, String[] columns, String selection) {
        return this.getResults(table, columns, selection, null, null, null, null, null);
    }

    public Cursor getResults(String table, String[] columns, String selection, String[] selectionArgs) {
        return this.getResults(table, columns, selection, selectionArgs, null, null, null, null);
    }

    public Cursor getResults(String table, String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
        return this.getResults(table, columns, selection, selectionArgs, null, null, orderBy, limit);
    }

    */
}
