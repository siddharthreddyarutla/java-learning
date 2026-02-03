
Redo and undo logs are indeed written to files stored within the database's file system, but they interact with memory structures differently from standard table data.

Redo Logs
* Storage: Redo logs are stored in physical files in the file system, often grouped into a dedicated redo log tablespace or directory
* Buffer Pool Presence: Redo logs are not stored in the primary database buffer pool (which caches data blocks from tables and indexes). Instead, changes are recorded in a separate, dedicated memory area called the redo log buffer (or redo buffer/log cache)
* Process: When an update query runs, the changes are made in the data buffer cache, and the redo information (the record of the change) is simultaneously written to the redo log buffer. This buffer is a sequential, in-memory queue that is regularly flushed to the physical redo log files on disk


  Undo Logs
* Storage: Undo information is stored in an undo tablespace (or undo segments/logs, depending on the database system) on disk [2].
* Buffer Pool Presence: Unlike redo logs, undo log records are treated as standard data blocks by the database management system.
* Process: The blocks of the undo tablespace are cached within the main database buffer pool alongside normal table data blocks. When an update is executed, the "before" image of the data (the undo information) is generated and placed into an undo block in the buffer pool, just like a standard data modification. This process also generates a corresponding redo log entry to ensure the writing of the undo information itself is recoverable
  Summary of How Much is in the Buffer Pool
* Redo Logs: None. They use their own dedicated memory structure (the redo log buffer).
* Undo Logs: A variable amount. Since undo information is treated as regular data, its blocks compete for space in the main database buffer pool based on a Least Recently Used (LRU) algorithm and the overall size of the pool. The amount present at any given time depends entirely on the activity of the system and the buffer pool's configuration.





Redo and undo logs are indeed written to files stored within the database's file system, but they interact with memory structures differently from standard table data. 

Redo Logs
* Storage: Redo logs are stored in physical files in the file system, often grouped into a dedicated redo log tablespace or directory [1].
* Buffer Pool Presence: Redo logs are not stored in the primary database buffer pool (which caches data blocks from tables and indexes). Instead, changes are recorded in a separate, dedicated memory area called the redo log buffer (or redo buffer/log cache) [1, 2].
* Process: When an update query runs, the changes are made in the data buffer cache, and the redo information (the record of the change) is simultaneously written to the redo log buffer. This buffer is a sequential, in-memory queue that is regularly flushed to the physical redo log files on disk [1, 2]. 

Undo Logs
* Storage: Undo information is stored in an undo tablespace (or undo segments/logs, depending on the database system) on disk [2].
* Buffer Pool Presence: Unlike redo logs, undo log records are treated as standard data blocks by the database management system.
* Process: The blocks of the undo tablespace are cached within the main database buffer pool alongside normal table data blocks [2, 3]. When an update is executed, the "before" image of the data (the undo information) is generated and placed into an undo block in the buffer pool, just like a standard data modification [2, 3]. This process also generates a corresponding redo log entry to ensure the writing of the undo information itself is recoverable. 

Summary of How Much is in the Buffer Pool
* Redo Logs: None. They use their own dedicated memory structure (the redo log buffer).
* Undo Logs: A variable amount. Since undo information is treated as regular data, its blocks compete for space in the main database buffer pool based on a Least Recently Used (LRU) algorithm and the overall size of the pool [3]. The amount present at any given time depends entirely on the activity of the system and the buffer pool's configuration. 
