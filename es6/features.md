### Features added in ES6
1.  Assign values with or (||) in between such that in case of first falling, second value is assigned.
2. IIFE: immediately invoked function expression (func)()
3. Default parameterized functions
4. Spreading: distribute a list based given inputs [arguments] into each parameters required
5. Anti-spreading:  each arguments passed seperately, has been created as list when fetching as parameter in function.



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


