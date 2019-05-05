package martincostasravnserver;

public class Media
{
	private String type, title, author, date;
	private int id, viewCount, viewOrder;


	public Media(String type, String title, String author, String date, int id, int viewCount, int viewOrder)
	{
		this.type = type;
		this.title = title;
		this.author = author;
		this.date = date;
		this.id = id;
		this.viewCount = viewCount;
		this.viewOrder = viewOrder;
	}


	public String getType()
	{
		return type;
	}


	public void setType(String type)
	{
		this.type = type;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getAuthor()
	{
		return author;
	}


	public void setAuthor(String author)
	{
		this.author = author;
	}


	public String getDate()
	{
		return date;
	}


	public void setDate(String date)
	{
		this.date = date;
	}


	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public int getViewCount()
	{
		return viewCount;
	}


	public void setViewCount(int viewCount)
	{
		this.viewCount = viewCount;
	}


	public int getViewOrder()
	{
		return viewOrder;
	}


	public void setViewOrder(int viewOrder)
	{
		this.viewOrder = viewOrder;
	}
}
