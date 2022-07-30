package tdrtool;

public class isdatavalid {

	public boolean isimei(String instr){
		boolean resultboolean=false;
		boolean allinnum=true;
		boolean allinsamenum=true;
		if(instr.length()==15){
			for(int i=0;i<instr.length();i++)
				if("0123456789".indexOf(instr.substring(i, i+1))<0)
					allinnum=false;
			for(int i=0;i<instr.length()-1;i++)
				if(instr.substring(i, i+1)!=instr.substring(i, i+1))
					allinsamenum=false;
			if(allinnum && (!allinsamenum))
				resultboolean=true;
		}
		
		return resultboolean;
	}
}
