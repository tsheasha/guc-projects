:- style_check(-discontiguous).
:- use_module(library(lists)).

%course(X,Y) means that course X has Y groups.
course(csen403,4).
course(csen401,4).
%course(math401,4,[t07,t08,t09,t10,t11,all_group_4]).
course(elct401,4).
course(maths4,4).
course(comm401,4).
course(csen402,4).


totalcourses(6).


totalmeetings(csen403,5).
course_meetings(csen403,lab,t07,nada_sharaf,'lab for t07').
course_meetings(csen403,lab,t08,nada_sharaf,'lab for t08').
course_meetings(csen403,lab,t09,nada_sharaf,'lab for t09').
course_meetings(csen403,lab,t10,nada_sharaf,'lab for t10').

course_meetings(csen403,lecture,all_group_4,prof_slim,'lecture1').

totalmeetings(csen401,5).

course_meetings(csen401,lecture,all_group_4,prof_slim,'lecture').

course_meetings(csen401,lab,t07,nermeen_abdel_aziz,'lab for t07').
course_meetings(csen401,lab,t08,injy_khairy,'lab for t08').
course_meetings(csen401,lab,t09,nermeen_abdel_aziz,'lab for t09').
course_meetings(csen401,lab,t10,injy_khairy,'lab for t010').


totalmeetings(elct401,9).

course_meetings(elct401,lecture,all_group_4,dr_yasser_hegazy,'lecture 1').

course_meetings(elct401,lab,t07,ahmed_ragheb,'lab for t07').
course_meetings(elct401,lab,t08,samar_mohamed,'lab for t08').
course_meetings(elct401,lab,t09,ahmed_ragheb,'lab for t09').
course_meetings(elct401,lab,t10,samar_mohamed,'lab for t10').

course_meetings(elct401,tutorial,t07,yasmmine_hassan,'tutorial for t07').
course_meetings(elct401,tutorial,t08,yasmmine_hassan,'tutorial for t08').
course_meetings(elct401,tutorial,t09,sara_mohamed,'tutorial for t09').
course_meetings(elct401,tutorial,t10,sara_mohamed,'tutorial for t10').

totalmeetings(maths4,6).

course_meetings(maths4,tutorial,t07,khaled_mohamed,'tutorial for t07').
course_meetings(maths4,tutorial,t08,tarek_mounir,'tutorial for t08').
course_meetings(maths4,tutorial,t09,khaled_mohamed,'tutorial for t09').
course_meetings(maths4,tutorial,t10,hany_elsharkawy,'tutorial for t10').
course_meetings(maths4,lecture,all_group_4,prof_bauman,'lecture 1').
course_meetings(maths4,lecture,all_group_4,prof_bauman,'lecture 2').


totalmeetings(comm401,5).
course_meetings(comm401,tutorial,t07,mohamed_esameldin,'tutorial for t07').
course_meetings(comm401,tutorial,t08,sally_mahmoud,'tutorial for t08').
course_meetings(comm401,tutorial,t09,mohamed_esameldin,'tutorial for t09').
course_meetings(comm401,tutorial,t10,sally_mahmoud,'tutorial for t10').
course_meetings(comm401,lecture,all_group_4,dr_amr_talaat,'lecture 1').

totalmeetings(csen402,5).
course_meetings(csen402,tutorial,t07,nehal,'tutorial for t07').
course_meetings(csen402,tutorial,t08,nehal,'tutorial for t08').
course_meetings(csen402,tutorial,t09,noura_maghawry,'tutorial for t09').
course_meetings(csen402,tutorial,t10,noura_maghawry,'tutorial for t10').
course_meetings(csen402,lecture,all_group_4,dr_cherif_salama,'lecture 1').

labs([c7201,c6209,c7220]).
lecturehalls([h3]).
tutorialrooms([c6301,b4108,c2201,c2301,c2202,c2203]).

days([saturday,sunday,monday,tuesday,wednesday,thursday]).




%

group(4,[t07,t08,t09,t10,all_group_4]).


totalgroups(1).



