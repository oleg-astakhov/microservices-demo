FROM nginx:1.27.0
RUN mkdir -p /usr/local/frontend
COPY nginx/ /usr/local/frontend/
RUN chmod +x /usr/local/frontend/entrypoint.sh
COPY main /usr/share/nginx/html

CMD ["/usr/local/frontend/entrypoint.sh"]