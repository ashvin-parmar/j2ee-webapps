### Features added in ES6
1.  Assign values with or (||) in between such that in case of first falling, second value is assigned.
2. IIFE: immediately invoked function expression (func)()
3. Default parameterized functions
4. Spreading: distribute a list based given inputs [arguments] into each parameters required
5. Anti-spreading:  each arguments passed seperately, has been created as list when fetching as parameter in function.
6. Packing-Unpacking: Pack values while returning
7. Class creation in better way
8. Anonymous function or lambda expression


## Defined
1: Assign multiple values to a variable
var m=null;
var k=10;
var a=m || k;	//if m is undefined or null then default value k is assigned.

2: IIFE
(function(parm){
alert(parm)
})(value);		//Create a function and call the same time.

3: Defult parameterized function
function sam(a=10,b=20,c=30)
{
//Over here, if user of this function not provide the arguments then default values is taken as arguments from parameter.
}

4: Spreading
sam(...a);	//At the time of calling the function
		//Spreading the list based arguments into parameters seperately.

5: Anti-Spreading
function sam(...a)		//Take all the multiple arguments provided as a list.
{
var i=0;
for(i=0;i<a.length;a++)
{
alert(a[i]);	
}
}

6: Packing-Unpacking: 
function sam()
{
return [100,200,300];	//Packing
}
function doIt()
{
var [a,b,c]=sam();	//Unpacking
var [d,,e]=sam();
var [f,]=sam();		//Different ways
}

7: _Class creation feature.
function sam()
{
var b={
wattage:60,				//Set the values with ',' seperated
setWattage(wattage)
{
this.wattage=wattage;		//Create function inside without declaring it as 'function'
},
getWattage()
{
return this.wattage;
}
};
alert(b.getWattage());
b.setWattage(100);		//Calling method 
alert(b.wattage);
}

8: anonymous function or lambda expression

WAY1: 	Without return 
var a=(p,q)=>p+q;

WAY2: With return
var a=(p,q)=>{
return p+q;
};

