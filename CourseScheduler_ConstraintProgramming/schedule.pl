% Author:
% Date: 10/2/2012

:- compile('course_catalogue_10349.pl').

lecture(X):- 
    course_meetings(C,lecture,G,T,CODE), 
    X = slot(lecture,C,G,T,CODE).
    
tutorial(X):- 
    course_meetings(C,tutorial,G,T,CODE), 
    X = slot(tutorial,C,G,T,CODE).

lab(X):- 
    course_meetings(C,lab,G,T,CODE), 
    X = slot(lab,C,G,T,CODE).

section(X):- 
    course_meetings(C,TY,G,T,CODE), 
    X = slot(TY,C,G,T,CODE).

getGroup(S,G):- 
    S = slot(_,_,G,_,_).

groupMatch(G,G).

groupMatch(_,all_group_4).

hastutorial(C):- 
    course_meetings(C,tutorial,_,_,_).

hastutorialslot(S):- 
    S = slot(_,C,_,_,_), 
    hastutorial(C).
    
haslab(C):- 
    course_meetings(C,lab,_,_,_).

haslabslot(S):- 
    S = slot(_,C,_,_,_), 
    haslab(C).    
    
islab(S):- 
    S = slot(lab,_,_,_,_).

findGroupSlot(G,[H|T]):- 
    (getGroup(H,G1), groupMatch(G,G1)) ;
    findGroupSlot(G,T).

countGroupDay(_,[],0).

countGroupDay(G,[H|T],N):- 
    findGroupSlot(G,H), 
    countGroupDay(G,T,N1), 
    N is N1 + 1.

countGroupDay(G,[H|T],N):- 
    \+ findGroupSlot(G,H), 
    countGroupDay(G,T,N).
 
groupCheck1(_,[]).

groupCheck1(G,[H|T]) :- 
    countGroupDay(G,H,N), 
    totalslots(C1), 
    N < C1, 
    groupCheck1(G,T).

groupCheck2(_,[]).

groupCheck2(G,[H|T]) :- 
    countGroupDay(G,H,0) ;
    groupCheck2(G,T).

collectCourses(L):- 
    course(C,_), 
    member(C,L). 
    
collectCourses([C|T]):- 
    collectCourses(T), 
    course(C,_), 
    \+ member(C,T).

countType(_,[],0).

countType(X,[H|T],N):- 
    countType(X,T,N1), 
    H = slot(X,_,_,_,_), 
    N is N1+1.
    
countType(X,[H|T],N):- 
    countType(X,T,N), 
    H \= slot(X,_,_,_,_).

slotConflict(slot(_,_,G,_,_),slot(_,_,G,_,_)).

slotConflict(slot(_,_,_,T,_),slot(_,_,_,T,_)).

slotConflict(slot(X,_,_,_,_),slot(Y,_,_,_,_)):- 
    X = lecture ; 
    Y = lecture.

lectureConflict(slot(lecture,C,G,_,_),slot(lecture,C,G,_,_)).

lecturelabConflict(slot(lecture,C,_,_,_),slot(lab,C,_,_,_)).

lecturelabConflict(slot(lab,C,_,_,_),slot(lecture,C,_,_,_)).

lecturetutorialConflict(slot(lecture,C,_,_,_),slot(tutorial,C,_,_,_)).

lecturetutorialConflict(slot(tutorial,C,_,_,_),slot(lecture,C,_,_,_)).


tutoriallab(slot(lab,C,G,_,_),slot(tutorial,C,G,_,_)).

labwithtutorial(S):- 
    haslabslot(S),
    hastutorialslot(S).

compareSlot(X,[H|T]):- 
    slotConflict(X,H); 
    compareSlot(X,T).

compareSlot2(X,[H|T]):- 
    lectureConflict(X,H); 
    lecturetutorialConflict(X,H); 
    lecturelabConflict(X,H); 
    compareSlot2(X,T).

dayinvalid(S,[H|T]):- 
    compareSlot2(S,H) ; 
    dayinvalid(S,T).

slotInvalid(X,L):- 
    (X = slot(lab,_,_,_,_), 
    countType(lab,L,N), 
    labs(LA), 
    length(LA,N)) ; 
    compareSlot(X,L).
    
slotInvalid(X,L):- 
    (X = slot(tutorial,_,_,_,_), 
    countType(tutorial,L,N), 
    tutorialrooms(LA), 
    length(LA,N)) ;
    compareSlot(X,L).

insertSlot(S,L,[S|L]):- 
    \+ slotInvalid(S,L).

insertintoDay(S,[H|T],[H1|T]) :- 
    insertSlot(S,H,H1).
    
insertintoDay(S,[H|T],[H|T1]) :- 
    insertintoDay(S,T,T1). 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

insertintoDay(S,[],L1,N).

insertintoDay(S,[[[]|T1]|T2],L1,N):-
    N \= 0,
    insertintoDay(S,[T1|T2],L1,N).

insertintoDay(S,[[]|T2],L1,N):-
    N \= 0,
    insertintoDay(S,T2,L1,N).

insertintoDay(S,[[[H|T]|T1]|T2],L1,0):-
    L1 = [[[S|T]|T1]|T2].

insertintoDay(S,L,L1,N):-
    N1 is N - 1,
    L = [[[H|T]|T1]|T2],
    insertintoDay(S,[[T|T1]|T2],L1,N1).

%insertToDay(S,L,L1) :-
%    labwithtutorial(S),
%    S = slot(lab,G,_,_,_),
%    labwithtutorial(D),
%    D = slot(tutorial,G,_,_,_),
%    gettutorialslot(D,L,0,N),    
%    getlabslot(S,L,0,N1),
%    N1 > N,
%    insertintoDay(S,L,L1,N).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

insertToDay(S,[H|T],[H1|T]) :-
    \+ dayinvalid(S,H), 
    insertintoDay(S,H,H1).
    
insertToDay(S,[H|T],[H|T1]) :-
    insertToDay(S,T,T1).  

insertToschedule([],X,X).

insertToschedule([H|T],I,N) :-
    insertToDay(H,I,N1),
    getGroup(H,G), 
    groupCheck1(G,N1), 
    groupCheck2(G,N1),
    
    insertToschedule(T,N1,N).

removefirst([],[]).

removefirst([_|T],T).

schedule(S):-
    init(S1), 
    setof(Y,section(Y),ALL),
    insertToschedule(ALL,S1,S).

init(S) :- 
    initdays([],T), 
    initslots(T,S).

initdays(E,R):- 
    days(D), 
    length(D,L), 
    insertEmpty(E,R,L).
    
initslots(E,R):- 
    length(E,C1), 
    totalslots(C2), 
    insertEmpty2(E,R,C1,C2).

insertEmpty(X,X,0).

insertEmpty(L,R,C):- 
    C>0, 
    C1 is C-1, 
    insertEmpty([[]|L],R,C1).

insertEmpty2(X,X,0,_).

insertEmpty2([H|T],[H1|T1],C1,C2):- 
    C1>0, 
    CX is C1-1, 
    insertEmpty(H,H1,C2), 
    insertEmpty2(T,T1,CX,C2).    

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

gettutorialslot(_,[],N1,N):-
    N = N1.

gettutorialslot(_,[[]|_],N1,N):-
    N = N1.

gettutorialslot(_,[[[]|_]|_],_,_).
    
gettutorialslot(S,[[[H|T]|T1]|T2],N1,N):-
    H \= [],
    S = H,
    N = N1.

gettutorialslot(S,[[[H|_]|T1]|T2],N1,N):-
    (H \= [];
    S \= H),
    N2 is N1 + 1,
    gettutorialslot(S,[[T|T1]|T2],N2,N),
    gettutorialslot(S,[T1|T2],N2,N),
    gettutorialslot(S,T2,N2,N).

getlabslot(_,[],N1,N):-
    N = N1.

getlabslot(_,[[]|_],N1,N):-
    N = N1.

getlabslot(_,[[[]|_]|_],_,_).
    
getlabslot(S,[[[H|T]|T1]|T2],N1,N):-
    H \= [],
    S = H,
    N = N1.

getlabslot(S,[[[H|_]|T1]|T2],N1,N):-
    (H \= [];
    S \= H),
    N2 is N1 + 1,
    getlabslot(S,[[T|T1]|T2],N2,N),
    getlabslot(S,[T1|T2],N2,N),
    getlabslot(S,T2,N2,N).
