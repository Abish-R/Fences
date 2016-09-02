package helixtech.fences.com.model;

public class Post {
	
	String kind,type,title,url,desc,firstName,lastName;
	String postedTime;
	int noOfComments,noOfLikes,postId,likeStatus;
	String userId;
	String location,latitude,longitude,websitelink,applink,apppackagename;
	
	public Post() {
		
	}
	
	public Post(String kind, String type, String title, String url,
			String desc, String firstName, String lastName, String postedTime,
			int noOfComments, int noOfLikes, int postId, String userId,
			int likeStatus) {
		super();
		this.kind = kind;
		this.type = type;
		this.title = title;
		this.url = url;
		this.desc = desc;
		this.firstName = firstName;
		this.lastName = lastName;
		this.postedTime = postedTime;
		this.noOfComments = noOfComments;
		this.noOfLikes = noOfLikes;
		this.postId = postId;
		this.userId = userId;
		this.likeStatus = likeStatus;
	}

	

	public int getLikeStatus() {
		return likeStatus;
	}

	public void setLikeStatus(int likeStatus) {
		this.likeStatus = likeStatus;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPostedTime() {
		return postedTime;
	}

	public void setPostedTime(String postedTime) {
		this.postedTime = postedTime;
	}

	public int getNoOfComments() {
		return noOfComments;
	}

	public void setNoOfComments(int noOfComments) {
		this.noOfComments = noOfComments;
	}

	public int getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(int noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getLocation() {
		return location;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getWebpageLink() {
		return websitelink;
	}
	public String getAppGoogleLink() {
		return applink;
	}
	public String getAppPackage() {
		return apppackagename;
	}

	public void setLocation(String loc) {
		this.location=loc;
	}
	public void setLatitude(String lat) {
		this.latitude=lat;
	}
	public void setLongitude(String lon) {
		this.longitude=lon;
	}
	public void setWebpageLink(String weblink) {
		this.websitelink=weblink;
	}
	public void setAppGoogleLink(String glink) {
		this.applink=glink;
	}
	public void setAppPackage(String apppack) {
		this.apppackagename=apppack;
	}
	
	

}
