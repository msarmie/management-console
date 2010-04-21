<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:insertDefinition name="base-space">
	<tiles:putAttribute name="title">
		<spring:message code="space" />
	</tiles:putAttribute>
	<tiles:putAttribute name="menu">
	</tiles:putAttribute>
	<tiles:putAttribute name="main-content">
		<div dojoType="dijit.layout.ContentPane" region="top" splitter="false" class="main-content-header">
				<tiles:insertDefinition name="base-content-header">
					<tiles:putAttribute name="title">
						${space.spaceId}
					</tiles:putAttribute>
					<tiles:putAttribute name="subtitle">
						<table style="margin: 0; padding: 0;">
							<tr>
								<td>

								<ul class="breadcrumb">
									<li><a href="<c:url value="/spaces.htm"/>"><spring:message
										code="spaces" /></a> </li>
								</ul>


								</td>
								<td style="text-align: right">
								<ul class="action-list">
									<li><a class="add-content-item" spaceId="${space.spaceId}"
										href="<c:url value="/contents/add?spaceId=${space.spaceId}"/>"><spring:message
										code="add.contentItem" /></a></li>
									<li><a spaceId="${space.spaceId}" class="update-space"
										href="<c:url value="/space/changeAccess">
											<c:param name="spaceId" value="${space.spaceId}"/>
											<c:param name="returnTo" value="${currentUrl}"/>
										</c:url>">
										<spring:message
											code="${space.metadata.access == 'OPEN' ?'close.space' : 'open.space'}" />
									</a></li>
									<li><a  spaceId="${space.spaceId}" class="remove-space delete-action" href="<c:url value="removeSpace.htm">
											   		<c:param name="spaceId" value="${space.spaceId}"/>
											   		<c:param name="returnTo" value="${pageContext.request.contextPath}/spaces.htm"/>
											    </c:url>">
											<spring:message code="remove"/>
										</a>
									</li>
									<li><a  id="refresh" spaceId="${space.spaceId}" href="">
											<spring:message code="refresh"/>
										</a>
									</li>

								</ul>

								</td>
							</tr>
						</table>
					</tiles:putAttribute>
				</tiles:insertDefinition>

		</div>
   	    <div  dojoType="dijit.layout.ContentPane" region="left" splitter="true" id="menu-div">
			<tiles:insertTemplate
				template="/WEB-INF/jsp/layout/box-control.jsp">
				<tiles:putAttribute name="title">
					<spring:message code="space.details"/>
				</tiles:putAttribute>
				<tiles:putAttribute name="miniform" value="" />
				<tiles:putAttribute name="body">
					<script type="text/javascript">
						dojo.require("duracloud.durastore");
					</script>
				
					<table class="small extended-metadata">
						<tr>
							<td><spring:message code="access" /></td>
							<td><spring:message
								code="access.${fn:toLowerCase(space.metadata.access)}" /></td>
						</tr>
						<tr>
							<td><spring:message code="created" /></td>
							<td>${space.metadata.created}</td>
						</tr>
						<tr>
							<td><spring:message code="contentItem.count" /></td>
							<td>${space.metadata.count}</td>
						</tr>
	
					</table>
				</tiles:putAttribute>
			</tiles:insertTemplate>					
			<!-- extended metadata -->
			<div><tiles:insertTemplate
				template="/WEB-INF/jsp/layout/metadata-control.jsp">
				<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
				<tiles:putAttribute name="metadata" value="${space.extendedMetadata}" />
			</tiles:insertTemplate></div>
			<!-- tags -->
			<div><tiles:insertTemplate
				template="/WEB-INF/jsp/layout/tag-control.jsp">
				<tiles:putAttribute name="spaceId" value="${space.spaceId}" />
				<tiles:putAttribute name="tags" value="${space.metadata.tags}" />
			</tiles:insertTemplate></div>		
		</div>
		<div  dojoType="dijit.layout.BorderContainer" region="center" splitter="false" gutters="false">
   	    <div  dojoType="dijit.layout.ContentPane" region="top" splitter="false">
				<tiles:importAttribute name="contentStoreProvider" />
				<c:set var="contentStore"
					value="${contentStoreProvider.contentStore}" />
				<c:choose>
				<c:when test="${space.metadata.count == 0}">
					<p>This space is empty. <a
						href="contents/add?spaceId=${space.spaceId}"><spring:message
						code="add.contentItem" /> >></a></p>
				</c:when>
				<c:otherwise>
					<table class="small" >
						<tr>
							<td>
								<form action="contents.htm?spaceId=${space.spaceId}" onchange="submit();"
									method="post">
									<input type="text" name="viewFilter"  value="${contentItemList.viewFilter}"/> 
									<spring:message code="filterById" />
								</form>
							</td>
							<td style="text-align:right">
							<!-- ugly: should be cleaned up  -->
							<form action="contents.htm?spaceId=${space.spaceId}"
								method="post"><select id="mpp" name="mpp"
								onchange="submit()">
								<option <c:if test="${contentItemList.maxResultsPerPage == 5 }">selected</c:if>>5</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 10 }">selected</c:if>>10</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 25 }">selected</c:if>>25</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 50 }">selected</c:if>>50</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 100 }">selected</c:if>>100</option>
								<option <c:if test="${contentItemList.maxResultsPerPage == 200 }">selected</c:if>>200</option>

							</select> <label for="mpp">items per page</label></form>

							</td>
							<td style="text-align:right; vertical-align:middle">
								<c:if test="${contentItemList.previousAvailable or contentItemList.nextAvailable}">
									<ul class="horizontal-list">
		
										<c:choose>
											<c:when test="${contentItemList.previousAvailable}">
												<a title="first page"
													href="contents.htm?action=f&spaceId=${space.spaceId}">[first]</a>
			
												<a title="previous page"
													href="contents.htm?action=p&spaceId=${space.spaceId}">[previous]</a>
											</c:when>
											<c:otherwise>
												<span class="disabled">
													[first] [previous]
												</span>
											</c:otherwise>
										</c:choose>
		
		
										<c:choose>
											<c:when test="${contentItemList.nextAvailable}">
												<a title="next"
													href="contents.htm?action=n&spaceId=${space.spaceId}">[next]</a>
											</c:when>
											<c:otherwise>
												<span class="disabled">
													[next]
												</span>
		
											</c:otherwise>
										</c:choose>
										
									</ul>
								</c:if>
							</td>
						</tr>
					</table>
			</c:otherwise>	
			</c:choose>

		</div>		
   	    <div  dojoType="dijit.layout.ContentPane" region="center" splitter="false">
					<script type="text/javascript">
						dojo.require("duracloud.durastore");
	
						dojo.addOnLoad(function(){
							dojo.query(".content-item").forEach(function(div){
								duracloud.durastore.loadContentItem(div, dojo.attr(div.id, "spaceId"), dojo.attr(div.id, "contentId"));
							});

							dojo.query("#refresh").forEach(function(element){
								dojo.connect(element, "onclick", function(e){
									duracloud.storage.expireSpace(dojo.attr(e.target.id,"spaceId"));
								});
							});
							
						});
					</script>
					<c:forEach items="${contentItemList.contentItemList}" var="content"
						varStatus="status">
						<div  id="${content.encodedContentId}"   contentId="${content.contentId}" spaceId="${content.spaceId}"  class="content-item actionable-item">
						<table>
									<tr class="list-item-header">
										<td colspan="2">
											<a href="<c:url value="content.htm">
										              <c:param name="spaceId" value="${space.spaceId}"/>
												   	  <c:param name="contentId" value="${content.contentId}"/>
											      </c:url>">${content.contentId}</a>
										
										</td>
									</tr>

									<tr>
										<td  width="70%" >
											<div class="tiny-thumb">
												
											</div>
											<div class="content-item-metadata">
												
											</div>
											
											
										</td>

										<td>
											<div  class="actions highlight">
												<ul class="action-list">
													<li>
														<a
													href="<c:url value="content.htm">
												              <c:param name="spaceId" value="${space.spaceId}"/>
														   	  <c:param name="contentId" value="${content.contentId}"/>
													      </c:url>"><spring:message code="details" /></a>
											        </li>
													<c:if test="${content.viewerURL != null}">
														<li>
															<a target = "viewer" href="${content.viewerURL}"><spring:message code="view" /></a>
														</li>
													</c:if>
													<li>
														<a href="<c:url value="${content.downloadURL}"></c:url>"><spring:message code="download" /></a>
													</li>
													<li><a  class="delete-action remove-content-item" spaceId="${content.spaceId}" contentId="${content.contentId}"
														href="<c:url value="removeContent.htm"  >
														   		<c:param name="spaceId" value="${space.spaceId}"/>
														   		<c:param name="contentId" value="${content.contentId}"/>
														   		<c:param name="returnTo" value="${currentUrl}"/>
														    </c:url>" ><spring:message code="remove" /></a></li>
												</ul>
											</div>
										</td>
									</tr>
								</table>
								</div>
							</c:forEach>
				</div>
			</div>
	</tiles:putAttribute>
</tiles:insertDefinition>

