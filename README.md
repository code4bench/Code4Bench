# Code4Bench
Code4Bench: A Mutildimensional Benchmark of Codeforces Data for Different Program Analysis Techniques

code4bench is now available for download at http://doi.org/10.5281/zenodo.2582968

# installation (import)
1.	Download and unzip file from the given url
2.	Install mysql version 5.7
3.	Create database name it “code4bench”
4.	In MySQL Workbench <br>
  a.	Server->Data Import <br>
  b.	Select the extracted folder <br>
  c.	Push Start Import (it’s may take a time) <br>
5.	Finish

# Code4Bench Schema
The schema of Code4Bench is drawn below
![alt text](https://github.com/code4bench/Code4Bench/blob/master/db11.png)

# Fields definition
<table>
<thead><tr><th>Filed Name</th><th>Description</th></tr></thead><tbody>
 <thead><tr><th colspan="2" >source</th></tr></thead><tbody>
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>submission</td><td>ID number given by Codeforces to this submission</td></tr>
 <tr><td>sourceCode</td><td>The submitted source code</td></tr>
 <tr><td>author</td><td>ID number of submitter</td></tr>
 <tr><td>memory</td><td>The memory used by this submission</td></tr>
 <tr><td>time</td><td>The execution time of this submission</td></tr>
 <tr><td>sent</td><td>The submission time by user</td></tr>
 <tr><td>countLine</td><td>The number of lines of code</td></tr>
 <tr><td>problems_id</td><td>Problem ID number</td></tr>
 <tr><td>verdicts_id</td><td>The Codeforces' judgment on this submission</td></tr>
 <tr><td>languages_id</td><td>The language in which this submission is written</td></tr>
 <tr><td>isduplicated</td><td>The submission is unique or duplicated </td></tr>
 <thead><tr><th colspan="2" >verdicts</th></tr></thead><tbody>
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>name</td><td>The name of a judgment</td></tr>
 
 <thead><tr><th colspan="2" >languages</th></tr></thead><tbody> 
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>name</td><td>The name of a programming language</td></tr>
 
 <thead><tr><th colspan="2" >problems</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>fullname</td><td>The ID number of competition and name of problem</td></tr>
 <tr><td>contest</td><td>ID number of competition</td></tr>
 <tr><td>name</td><td>ID number of problem section</td></tr>
 <tr><td>context</td><td>The description of problem</td></tr>
 
 <thead><tr><th colspan="2" >testcases</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>inputData</td><td>Input data for problem</td></tr>
 <tr><td>expectedResult</td><td>Expected output for problem</td></tr>
 <tr><td>problems_id</td><td>ID number of corresponding problem</td></tr>
 <tr><td>isValid</td><td>Whether test case is complete or deficient</td></tr>
 
 <thead><tr><th colspan="2" >user</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>author_id</td><td>The ID of user</td></tr>
 <tr><td>gender</td><td>The user gender</td></tr>
 <tr><td>age</td><td>The user age</td></tr>
 <tr><td>country</td><td>The country in which the user lives</td></tr>
 <tr><td>state</td><td>The state in which the user lives</td></tr>
 <tr><td>city</td><td>The city in which the user lives</td></tr>
 <tr><td>mainJob</td><td>Is programming the user's main job</td></tr>
 <tr><td>t0_4</td><td>Does you work in time interval 00:00 to 04:00</td></tr>
 <tr><td>t4_8</td><td>Does you work in time interval 04:00 to 08:00</td></tr>
 <tr><td>t8_12</td><td>Does you work in time interval 08:00 to 12:00</td></tr>
 <tr><td>t12_16</td><td>Does you work in time interval 12:00 to 16:00</td></tr>
 <tr><td>t16_20</td><td>Does you work in time interval 16:00 to 20:00</td></tr>
 <tr><td>t20_24</td><td>Does you work in time interval 20:00 to 24:00</td></tr>
 <tr><td>single</td><td>Are you single?</td></tr>
 <tr><td>married</td><td>Are you married?</td></tr>
 <tr><td>divorced</td><td>Are you divorced?</td></tr>
 <tr><td>oneChild</td><td>Do you have one child?</td></tr>
 <tr><td>twoChild</td><td>Do you have two children?</td></tr>
 <tr><td>moreChild</td><td>Do you have more than two children</td></tr>
 <tr><td>educationLevel</td><td>Education level from diploma to PhD</td></tr>
 <tr><td>isFieldCS</td><td>Have you graduated in computer science?</td></tr>
 <tr><td>yearsWork</td><td>How many years have you been programming?</td></tr>
 <tr><td>hours_per_month</td><td>How many hours do you work in a month?</td></tr>
 <tr><td>teamOrAlone</td><td>Do you wok alone or as a team member?</td></tr>
 
 <thead><tr><th colspan="2" >countries</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>sortName</td><td>Abbr. of each country on which names are sorted</td></tr>
 <tr><td>name</td><td>The full name of a country</td></tr>
 <tr><td>phoneCode</td><td>The area code of a country</td></tr>
 
 <thead><tr><th colspan="2" >states</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>name</td><td>The names of a state</td></tr>
 <tr><td>country_id</td><td>The country ID of each state</td></tr>
 
 <thead><tr><th colspan="2" >cities</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>name</td><td>The names of a city</td></tr>
 <tr><td>state_id</td><td>The state ID of each city</td></tr>
 
 <thead><tr><th colspan="2" >Realfaultslocations AND realfaultslocations_c_cpp</th></tr></thead><tbody>
 
 <tr><td>id</td><td>A unique number</td></tr>
 <tr><td>subAccepted</td><td>ID number assigned by Codeforces' website to this accepted submission</td></tr>
 <tr><td>subWrong</td><td>ID number assigned by Codeforces' website to this faulty submission</td></tr>
 <tr><td>change</td><td>The number of lines which have been changed</td></tr>
 <tr><td>changeRate</td><td>The percentage of line which have been changed</td></tr>
 <tr><td>insert</td><td>The number of lines which have been added</td></tr>
 <tr><td>insertRate</td><td>The percentage of line which have been added</td></tr>
 <tr><td>delete</td><td>The number of lines which have been deleted</td></tr>
 <tr><td>deleteRate</td><td>The percentage of line which have been deleted</td></tr>
 <tr><td>faultLocations</td><td>The locations of faults in faulty version relative to the correct version</td></tr>
 <tr><td>countFaults</td><td>The number of faults in faulty version relative to the correct version</td></tr>
 <tr><td>countInsertFaults</td><td>The number of addition-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>countDeleteFaults</td><td>The number of deletion-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>countChangeFaults</td><td>The number of change-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>insertFaultsLocations</td><td>The locations of addition-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>changeFaultsLocations</td><td>The locations of change-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>deleteFaultsLocations</td><td>The locations of delete-type faults in faulty version relative to the correct version</td></tr>
 <tr><td>wSimA</td><td>The percentage at which the faulty version is similar to the correct version</td></tr>
 <tr><td>aSimW</td><td>The percentage at which the correct version is similar to the faulty version</td></tr>
 <tr><td>matchLines</td><td>The number of identical lines between the faulty and correct versions</td></tr>
</tbody></table>

# Data in Code4Cench
The number of submissions for each programming language are listed below

<table>
<thead><tr><th>ID</th><th>Language</th><th>Submission Count</th></tr></thead><tbody>
 <tr><td>1</td><td>GNU C++ 14</td><td>604,155</td></tr>
 <tr><td>2</td><td>GNU C</td><td>93,492</td></tr>
 <tr><td>3</td><td>MS C++</td><td>164,912</td></tr>
 <tr><td>4</td><td>GNU C++ 11</td><td>906,811</td></tr>
 <tr><td>5</td><td>FPC</td><td>47,522</td></tr>
 <tr><td>6</td><td>GNU C++</td><td>1,167,214</td></tr>
 <tr><td>7</td><td>Java 8</td><td>154,087</td></tr>
 <tr><td>8</td><td>Python 3</td><td>52,433</td></tr>
 <tr><td>9</td><td>Go</td><td>3,011</td></tr>
 <tr><td>10</td><td>D</td><td>742</td></tr>
 <tr><td>11</td><td>MS C#</td><td>14,896</td></tr>
 <tr><td>12</td><td>GNU C 11</td><td>18,574</td></tr>
 <tr><td>13</td><td>Python 2</td><td>36,469</td></tr>
 <tr><td>14</td><td>PyPy 2</td><td>4,507</td></tr>
 <tr><td>15</td><td>Ruby</td><td>3,806</td></tr>
 <tr><td>16</td><td>PHP</td><td>2,570</td></tr>
 <tr><td>17</td><td>PyPy 3</td><td>3,222</td></tr>
 <tr><td>18</td><td>Delphi</td><td>9,698</td></tr>
 <tr><td>19</td><td>Kotlin</td><td>4,739</td></tr>
 <tr><td>20</td><td>JavaScript</td><td>3,020</td></tr>
 <tr><td>21</td><td>Haskell</td><td>3,585</td></tr>
 <tr><td>22</td><td>OCaml</td><td>543</td></tr>
 <tr><td>23</td><td>Scala</td><td>2,131</td></tr>
 <tr><td>24</td><td>Mono C#</td><td>5,199</td></tr>
 <tr><td>25</td><td>Java 7</td><td>27,931</td></tr>
 <tr><td>26</td><td>Rust</td><td>599</td></tr>
 <tr><td>27</td><td>Perl</td><td>784</td></tr>
 <tr><td>28</td><td>GNU C++ 11</td><td>1,083</td></tr>
 <tr><td>29</td><td>Java 8 ZIP</td><td>107</td></tr>
 <tr><td>30</td><td>J</td><td>2,673</td></tr>
 <tr><td>31</td><td>GNU C++ 0X</td><td>34,746</td></tr>
 <tr><td>32</td><td>Java 6</td><td>22,988</td></tr>
 <tr><td>33</td><td>Pike</td><td>4,076</td></tr>
 <tr><td>34</td><td>Befunge</td><td>4,343</td></tr>
 <tr><td>35</td><td>Cobol</td><td>2,114</td></tr>
 <tr><td>36</td><td>Factor</td><td>2,606</td></tr>
 <tr><td>37</td><td>Secret-171</td><td>158</td></tr>
 <tr><td>38</td><td>Roco</td><td>3,136</td></tr>
 <tr><td>39</td><td>Tcl</td><td>3,752</td></tr>
 <tr><td>40</td><td>F#</td><td>15</td></tr>
 <tr><td>41</td><td>Io</td><td>2,908</td></tr>
</tbody></table>

    
      
