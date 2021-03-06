package gameClient;
import api.edge_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.Objects;

/**
 * this class represent an instance of a single pokemon inside the game
 */
public class CL_Pokemon {
	private edge_data _edge;
	private double _value;
	private int _type;
	private Point3D _pos;
	private double min_dist;
	private int min_ro;
	private CL_Agent nxtEater;
	private CL_Pokemon clspkm;
	private int closePkm;
	public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
		_type = t;
		_value = v;
		set_edge(e);
		_pos = p;
		min_dist = Integer.MAX_VALUE;
		min_ro = -1;
		nxtEater= null;
		closePkm= 0;

	}

	public int getClosePkm() {
		return closePkm;
	}
	public void setClosePkm(){
		this.closePkm++;
	}

	/**
	 * initializes the pokemon from a json file
	 * @param json
	 * @return object of pokemon
	 */
	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}

	public String toString() {return "F:{v="+_value+", t="+_type+"}";}
	public edge_data get_edge() {
		return _edge;
	}

	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}


	public Point3D getLocation() {
		return _pos;
	}
	public int getType() {return _type;}
//	public double getSpeed() {return _speed;}
	public double getValue() {return _value;}

	public double getMin_dist() {
		return min_dist;
	}

	public void setMin_dist(double mid_dist) {
		this.min_dist = mid_dist;
	}

	public CL_Agent getNxtEater() {
		return nxtEater;
	}

	public void setNxtEater(CL_Agent nxtEater) {
		this.nxtEater = nxtEater;
	}

	public CL_Pokemon getClspkm() {
		return clspkm;
	}

	public void setClspkm(CL_Pokemon clspkm) {
		this.clspkm = clspkm;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CL_Pokemon that = (CL_Pokemon) o;
		return Objects.equals(_pos, that._pos);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(_pos);//Objects.hash(_value, _type, _pos);
	}
	public void setMin_ro(int min_ro) {
		this.min_ro = min_ro;
	}
}
