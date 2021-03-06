/* UpdateReference.java
 * Author: nina
 * Date: Mar 28, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package org.toxbank.rest.protocol.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.projects.db.UpdateProjectMembership;

public class UpdateProtocol extends AbstractObjectUpdate<DBProtocol>{
	protected UpdateProjectMembership updateProject;
	private ReadProtocol.fields[] f = new ReadProtocol.fields[] {
			ReadProtocol.fields.title,
			ReadProtocol.fields.anabstract,
			ReadProtocol.fields.summarySearchable,
			ReadProtocol.fields.filename,
			ReadProtocol.fields.idorganisation,
			ReadProtocol.fields.iduser,
			ReadProtocol.fields.status,
			ReadProtocol.fields.published
	};
	public static final String update_sql = "update protocol set updated=now(),%s where idprotocol=? and version=?";


	public UpdateProtocol(DBProtocol ref) {
		super(ref);
		updateProject = new UpdateProjectMembership();
		updateProject.setObject(ref);
	}
	
	public UpdateProtocol() {
		this(null);
	}			
	
	@Override
	public void setObject(DBProtocol object) {
		super.setObject(object);
		if (updateProject!=null) updateProject.setObject(object);
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (index == 0) {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
	
			for (ReadProtocol.fields field: f) 
				if (field.getValue(getObject())!=null)
					params1.add(field.getParam(getObject()));
			
			if (params1.size()==0) throw new AmbitException("Nothing to update!");
			params1.add(ReadProtocol.fields.idprotocol.getParam(getObject()));
			params1.add(ReadProtocol.fields.version.getParam(getObject()));
			return params1;
		} else 
			//we want to remove projects if nothing set
			return updateProject.getParameters(index-1);
		
	}
	public String[] getSQL() throws AmbitException {

		StringBuilder b = new StringBuilder();
		String d = " ";
		for (ReadProtocol.fields field: f) 
			if (field.getValue(getObject())!=null) {
				switch (field) {
				case anabstract: {
					b.append(String.format("%s%s=?",d,"abstract"));
					break;
				}
				default: {	
					b.append(String.format("%s%s=?",d,field.name()));
				}
				}
				d = ",";
			}

			String[] sql = updateProject.getSQL();
			String[] result = new String[sql.length+1];
			result[0] = String.format(update_sql,b.toString());
			for (int i=0; i < sql.length; i++)
				result[i+1] = sql[i];
			return  result;
	}
	public void setID(int index, int id) {
			
	}
}
