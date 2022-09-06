# halyk-bt-records
## Installation and Run _App_
After cloning repository, need to add location to run application.
Exception to notified about location
```
Exception in thread "main" java.lang.RuntimeException: Need add filepath to CLI. Example: "local\path.csv"
at kz.halyk.App.main(App.java:24)
```

## Compute Service Analyze

After my issues I use Object with params and putted to List and Set. But I wanted to refactor code using hashmaps. 

So Console output looks like this
```
[main] INFO kz.halyk.service.ComputingService - Data processing started...
[Profile Memory] INFO kz.halyk.profiler.ProfileMemory - Profile Memory started...
[main] INFO kz.halyk.profiler.ProfileTimer -  Profile Timer Computer Service started...
[main] INFO kz.halyk.service.ComputingService - Data processing is finished...
[main] INFO kz.halyk.profiler.ProfileTimer - Computer Service stopped...
[main] INFO kz.halyk.profiler.ProfileTimer -  Profile Timer Profile Time (Computer Service) Results
[main] INFO kz.halyk.profiler.ProfileTimer -  Profile Timer 1. 0 min 17 sec
[main] INFO kz.halyk.profiler.ProfileMemory - Information about JVM Memory
[main] INFO kz.halyk.profiler.ProfileMemory - Max: 204 MB	Median: 110 MB
< 5                      0sec
>>>>> 53                 1sec
>>>>>>>>>>>>>>> 157      2sec
>>>>>>>>>>>>>>>>>>>> 204 3sec
>>>>>>> 76               4sec
>>>>>>>>>>>>>>> 156      5sec
>>>>>>>>>>>>>>>>> 173    6sec
>>>>>>>>>>>>>>> 156      7sec
>>> 30                   8sec
>>>>>>>>>>>>>> 145       9sec
>>>>> 50                 10sec
>>>>>>>>>>>>> 138        11sec
>> 22                    12sec
>>>>>>>>>>>>>>> 151      13sec
>>>>> 57                 14sec
>>>>>>>>>>>>>>>>> 176    15sec
>>>>>>>>> 90             16sec
>>>>>>>>>>>>>> 142       17sec

[pool-1-thread-1] INFO kz.halyk.profiler.ProfileMemory - pool-1-thread-1 is disabled...
```

## Output 
After computing data, csv file will save in `src/main/resources/csv_data/output.csv`