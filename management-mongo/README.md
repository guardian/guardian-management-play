# Guardian Management MongoDB

Introduced from version 5.5, this brings metric code into a common project so that any Scala based applications using the Manangement toolset can capture request information on their MongoDB database.

## Usage

Extend MongoManagement

    object MongoDataSource extends MongoManagement

Call wireIn once you have the connection object

    wireInTimingMetric(connection)

Add the metric to your management pages collection

    import com.gu.management.mongodb._
    new StatusPage("Your app name", MongoRequests :: Nil) ::