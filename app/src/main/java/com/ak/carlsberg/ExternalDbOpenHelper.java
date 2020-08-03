package com.ak.carlsberg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

    public static String DB_PATH;
	public static String DB_NAME;
	public SQLiteDatabase database;
	public final Context context;
	
	final String dbcarlsberg = "carlsberg.db";

    final String DIR_SD = "MonolitAgent/Database";
	final String DIR_SD_CARLSBERG = "Carlsberg/Database";

	public SQLiteDatabase getDb() {
		return database;
	}

	public ExternalDbOpenHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;

		File sdPath = Environment.getExternalStorageDirectory();
		DB_NAME = databaseName;
        if (DB_NAME == dbcarlsberg)
        {DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_NAME);}
        else
        {DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD + "/", DB_NAME);}

		openDataBase();
	}


	public void createDataBase() {
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Ошибка копирования базы");
				throw new Error("Ошибка еще какая-то");
			}
        } else {
            Log.i(this.getClass().toString(), "Тут тоже что-то непонятное");
		}
	}

    private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLException e) {
			Log.e(this.getClass().toString(), "И тут та же хрень");
		}

        if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}
		private void copyDataBase() throws IOException {

            InputStream externalDbStream = context.getAssets().open(DB_NAME);

	        String outFileName = DB_PATH + DB_NAME;

            OutputStream localDbStream = new FileOutputStream(outFileName);

			byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        createDataBase();
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase db) {}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("UPDATE PlanCalc SET Kofficient='0.30' WHERE PercentFact='91';");
		db.execSQL("UPDATE PlanCalc SET Kofficient='0.35' WHERE PercentFact='92';");
		db.execSQL("UPDATE PlanCalc SET Kofficient='0.40' WHERE PercentFact='93';");
		db.execSQL("UPDATE PlanCalc SET Kofficient='0.45' WHERE PercentFact='94';");
		db.execSQL("DROP TABLE Mhl;");
		db.execSQL("CREATE TABLE `Mhl` (" +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				" `UnionGrup` TEXT, " +
				" `SkuName` TEXT, " +
				" `SkuCode` TEXT, " +
				" `SabbrendName` TEXT, " +
				" `territory` TEXT, " +
				" `freeze` TEXT, " +
				" `rozliv` TEXT, " +
				" `TT11` TEXT, " +
				" `TT12` TEXT, " +
				"`TT21` TEXT, " +
				"`TT31` TEXT, " +
				"`TT32` TEXT, " +
				"`TT33` TEXT, " +
				"`TT41` TEXT, " +
				"`TT51` TEXT, " +
				"`TT61` TEXT, " +
				"`TT71` TEXT, " +
				"`TT81` TEXT, " +
				"`MT91` TEXT, " +
				"`MT101` TEXT, " +
				"`MT111` TEXT, " +
				"`MT112` TEXT, " +
				"`MT113` TEXT, " +
				"`MT114` TEXT, " +
				"`MT121` TEXT, " +
				"`MT122` TEXT, " +
				"`MT123` TEXT, " +
				"`MT124` TEXT, " +
				"`MT131` TEXT, " +
				"`MT132` TEXT, " +
				"`MT133` TEXT, " +
				"`MT134` TEXT, " +
				"`MT141` TEXT, " +
				"`OT161` TEXT, " +
				"`OT162` TEXT, " +
				"`OT171` TEXT, " +
				"`OT172` TEXT, " +
				"`OT173` TEXT, " +
				"`OT174` TEXT, " +
				"`OT175` TEXT, " +
				"`OT176` TEXT, " +
				"`OT177` TEXT, " +
				"`OT178` TEXT, " +
				"`OT179` TEXT, " +
				"`OT181` TEXT, " +
				"`OT182` TEXT, " +
				"`OT183` TEXT, " +
				"`OT191` TEXT, " +
				"`OT192` TEXT, " +
				"`OT193` TEXT, " +
				"`OT194` TEXT, " +
				"`OT195` TEXT, " +
				"`OT196` TEXT, " +
				"`OT197` TEXT, " +
				"`OT198` TEXT, " +
				"`OT199` TEXT, " +
				"`OT201` TEXT, " +
				"`OT202` TEXT, " +
				"`OT203` TEXT, " +
				"`OT204` TEXT, " +
				"`OT205` TEXT " +
				");");
		db.execSQL("DROP TABLE VisitsSalary;");
		db.execSQL("CREATE TABLE `VisitsSalary` (" +
				"`idauto` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				"`id` TEXT UNIQUE, " +
				"`ClientAddressId` TEXT, " +
				"`PlanDate` TEXT, " +
				"`Latitude` TEXT, " +
				"`Longitude` TEXT, " +
				"`LatitudeComplete` TEXT, " +
				"`LongitudeComplete` TEXT, " +
				"`Distance` TEXT, " +
				"`Confirmation` TEXT, " +
				"`Equipment` TEXT DEFAULT 25, " +
				"`Send` INTEGER DEFAULT 0, " +
				"`Updated` INTEGER DEFAULT 0" +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `BrendPrioritets` ( " +
				"`id` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				"`name`TEXT, " +
				" `type` TEXT, " +
				" `price` TEXT, " +
				" `number` TEXT " +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `RekStock` ( " +
				" `id` TEXT UNIQUE, " +
				" `PlanDate` TEXT, " +
				" `ClientAddresId` TEXT, " +
				" `Stockplan` TEXT, " +
				" `Stockfact` TEXT, " +
				" `Rekplan` TEXT, " +
				" `Rekfact` TEXT, " +
				" `Confirmation` TEXT, " +
				" `Send` INTEGER DEFAULT 0, " +
				" `Updated` INTEGER DEFAULT 0 " +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `Brends` ( " +
                " `id` TEXT UNIQUE, " +
				" `PlanDate` TEXT, " +
				" `ClientAddresId` TEXT, " +
				" `brend` TEXT , " +
				" `Confirmation` INTEGER DEFAULT 1, " +
				" `PersonConf` TEXT, " +
				" `Send` INTEGER DEFAULT 0, " +
				" `Updated` INTEGER DEFAULT 0, " +
				" `comment` TEXT " +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `predprosro` ( " +
				" `skucode` TEXT UNIQUE, " +
				" `number` TEXT " +
				");");
		db.execSQL(" ALTER TABLE `Brends` RENAME TO TempOldTable;");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Brends` ( " +
                " `id` TEXT UNIQUE, " +
                " `PlanDate` TEXT, " +
                " `ClientAddresId` TEXT, " +
                " `brend` TEXT , " +
                " `Confirmation` INTEGER DEFAULT 1, " +
                " `PersonConf` TEXT, " +
                " `Send` INTEGER DEFAULT 0, " +
                " `Updated` INTEGER DEFAULT 0, " +
                " `comment` TEXT, " +
                " `type` TEXT, " +
                " `price` TEXT " +
                ");");
        db.execSQL("INSERT INTO Brends (id,PlanDate,ClientAddresId,brend,Confirmation,PersonConf,Send,Updated,comment,type,price) " +
                " SELECT id,PlanDate,ClientAddresId,brend,Confirmation,PersonConf,Send,Updated,comment,null,null FROM TempOldTable;");
        db.execSQL("DROP TABLE TempOldTable;");
		db.execSQL("CREATE TABLE IF NOT EXISTS `Stock` ( " +
				" `id` TEXT UNIQUE, " +
				" `PlanDate` TEXT, " +
				" `ClientAddresId` TEXT, " +
				" `WareId` TEXT , " +
				" `WareName` TEXT, " +
				" `Fact` TEXT "+
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `PromoDespatch` ( " +
				" `id` TEXT UNIQUE, " +
				" `PlanDate` TEXT, " +
				" `ClientAddresId` TEXT, " +
				" `Quantity` TEXT  " +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `PromoTyped` ( " +
				" `id` TEXT UNIQUE, " +
				" `PlanDate` TEXT, " +
				" `ClientAddresId` TEXT, " +
				" `InfoValue` TEXT  " +
				");");
		db.execSQL("CREATE TABLE IF NOT EXISTS `RLP_plan` (  `ClientAddressId` TEXT UNIQUE,  `mai3` TEXT,   `mai4` TEXT,   `mai5` TEXT,   `mai6` TEXT,   `mai7` TEXT,   `june3` TEXT,   `june4` TEXT,   `june5` TEXT,   `june6` TEXT,   `june7` TEXT,   `july3` TEXT,   `july4` TEXT,   `july5` TEXT,   `july6` TEXT,   `july7` TEXT,   `august3` TEXT,   `august4` TEXT,   `august5` TEXT,   `august6` TEXT,   `august7` TEXT,   `september3` TEXT,   `september4` TEXT,   `september5` TEXT,   `september6` TEXT,   `september7` TEXT,   `super0_5` TEXT,   `super1_0` TEXT,   `super1_5` TEXT,   `super2_0` TEXT,   `super2_5` TEXT  );");
		db.execSQL("CREATE TABLE IF NOT EXISTS `RLP_fact` (  `ClientAddressId` TEXT UNIQUE,  `mai` TEXT,   `june` TEXT,   `july` TEXT,   `august` TEXT,   `september` TEXT,   `super` TEXT  );");

        db.setVersion(newVersion);
	}
}