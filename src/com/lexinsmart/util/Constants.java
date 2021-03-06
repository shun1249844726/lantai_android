 package com.lexinsmart.util;

public class Constants {
	public static String gUserName = "蓝泰助手";
	public static final int DB_COLUMN_LEN = 100;
	public static final String DB_USER = "USERINFO";
	public static final String DB_THING = "THINGS";
	public static final String[] DB_USER_COLUMNS = new String[] { "username",
			"password" };
	public static final String[] DB_THING_COLUMNS = new String[] { "plantNum",
			"plantName", "roadNum", "roadDetails" };
	public static final String SUBSTR_MSG = "err_msg";
	public static final String SUBSTR_UN = DB_USER_COLUMNS[0];
	public static final String SUBSTR_PW = DB_USER_COLUMNS[1];

	public static final String SUBSTR_PLANTNUM = DB_THING_COLUMNS[0];
	public static final String SUBSTR_PLANTNAME = DB_THING_COLUMNS[1];
	public static final String SUBSTR_ROADNUM = DB_THING_COLUMNS[2];
	public static final String SUBSTR_ROADDETAILS = DB_THING_COLUMNS[3];

	public static final String[] SUBSTR_THINGS = new String[] {
			Constants.SUBSTR_UN, Constants.SUBSTR_PW,
			Constants.SUBSTR_PLANTNUM, Constants.SUBSTR_PLANTNAME,
			Constants.SUBSTR_ROADNUM, Constants.SUBSTR_ROADDETAILS };
	public static final String[] ROADS = new String[] { "Road1", "Road2",
			"Road3", "Road4", "Road5", "Road6", "Road7", "Road8", "Road9",
			"Road10", "Road11", "Road12", "Road13", "Road14", "Road15",
			"Road16", "Road17", "Road18", "Road19", "Road20", "Road21",
			"Road22", "Road23", "Road24", "Road25", "Road26", "Road27",
			"Road28", "Road29", "Road30", "Road31", "Road32", "Road33",
			"Road34", "Road35", "Road36", "Road37", "Road38", "Road39",
			"Road40", "Road41", "Road42", "Road43", "Road44", "Road45",
			"Road46", "Road47", "Road48", "Road49", "Road50", "Road51",
			"Road52", "Road53", "Road54", "Road55", "Road56", "Road57",
			"Road58", "Road59", "Road60", "Road61", "Road62", "Road63",
			"Road64", "Road65", "Road66", "Road67", "Road68", "Road69",
			"Road70", "Road71", "Road72", "Road73", "Road74", "Road75",
			"Road76", "Road77", "Road78", "Road79", "Road80", "Road81",
			"Road82", "Road83", "Road84", "Road85", "Road86", "Road87",
			"Road88", "Road89", "Road90", "Road91", "Road92", "Road93",
			"Road94", "Road95", "Road96", "Road97", "Road98", "Road99",
			"Road100", "Road101", "Road102" };
	/*
	 * [Road1] AreaId = 1 RoadId = 1 RoadName = 兴港路与龙山路 SignallerId =
	 * 9DCABB5255C50C01 Longitude = 121.1512806 Latitude = 28.08795833
	 * HxLongitude = 121.155520677567 HxLatitude = 28.084787935507 PhaseInfo =
	 * 105103101102108103101111104103102101107105101111101105102104106107101111102107102101103107103100
	 * 区域编号， 路口编号 路口名称 信号机编号 地球坐标（经度/纬度） 火星坐标（经度/纬度） 相位信息
	 */
	public static final String[] ONEROADDTL = new String[] { "AreaId",
			"RoadId", "RoadName", "SignallerId", "Longitude", "Latitude",
			"HxLongitude", "HxLatitude", "PhaseInfo" };
	// PlanManager = 1;Map=2;

	public static int ACTIVITYFLAG = 0;
	public static int EDITROADFLAG = 0;
	public static String[][] Roads;

	public static byte[] GETGPS = { 0x7E, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x03, 0x76, 0x7E };
//	public static String IP ="192.168.43.1";
//	public static int PORT = 5001;
	
	public static String IP ="192.168.234.1";
	public static int PORT = 65317;
	public static String SOCKETSTATE;
	
	public static int  FLEETLENGTH=5,FLEETSPACING=15, SENDCOMMOND_DISTENCE=400;
}
