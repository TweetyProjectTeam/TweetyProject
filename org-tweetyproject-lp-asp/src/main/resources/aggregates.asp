course(databases).
:- 12<= #sum { 6 : course(ai); 6 : course(databases);  4: course(networks); 12 : course(project); 3 : course(xml) }.
:- 1 <= #count {course(databases); course(ai); course(networks) } <= 2.  
:- 3<= #min { 6 : course(databases); 6 : course(ai); 12 : course(project); 3 : course(xml); 4 : course(networks) }.
