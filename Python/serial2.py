
import sys
import Image
import ImageDraw
import math
import operator

def getGradient(image, (w,h), draw):

    gradients = {}
    vectors = {}
    histograms = {}
    for x in range(0, w):
        for y in range(0, h):
            if x == 0 or x == w - 1 or y == 0 or y == h - 1:
                x_vec = 0
                y_vec = 0
            else:
                x_vec = image[x + 1, y] - image[x - 1, y]
                y_vec = image[x, y + 1] - image[x, y - 1]

            v = pow(x_vec, 2) + pow(y_vec, 2)            
            mag = pow( v, 0.5)
            ang = math.atan2(y_vec, x_vec)

            gradients[x, y] = (mag, ang)

            vector = vectors.get((x//8,y//8), (0,0))

            vectors[x/8,y/8] = map(operator.add, vector,[x_vec,y_vec])

            angRad = math.degrees(ang)
            
            histogram = histograms.get((x//8, y//8), 9 * [0])
            pos1 = ((x + 10) // 20 - 1) % 9
            pos2 = ((x + 10) // 20) % 9
           
            histogram[pos1] += ((20 - ((x % 10.0) % 20)) / 20) * mag
            histogram[pos2] += (1 - (20 - ((x % 10.0) % 20)) / 20) * mag
              
            histograms.update({(x//8, y//8) : histogram})

    totalSize = 0
    output = []
            
    for x in range(0, w // 8 - 1):
        for y in range(0, h // 8 - 1):
            block = histograms.get((x,y)) \
               + histograms.get((x + 1, y)) \
               + histograms.get((x, y + 1)) \
               + histograms.get((x + 1, y + 1))

            mag = 0
            for i in range(0, len(block)):
                mag += block[i]
            if mag != 0:
                for i in range(0, len(block)):
                    block[i] /= mag
            output += block
            totalSize += len(block)

    print 64
    print totalSize
    print output
    
  
    for x in range (0, w//8):
        for y in range(0,h//8):        
            [x_vec, y_vec] = vectors.get((x,y), (0,0))
            
            v = pow(x_vec, 2) + pow(y_vec, 2)
            mag = pow( v, 0.5)
            if(mag > 0):
                x_vec = x_vec / 500
                y_vec = y_vec / 500

                aux = x_vec
                x_vec = y_vec
                y_vec = -aux
    
                drawVector(draw, (x * 8 + 4, y * 8 + 4),(x_vec + x * 8 + 4,y_vec + y * 8 + 4))
   

def drawVector(draw, (x1, y1), (x2, y2)):
    draw.line((x1,y1,x2,y2), width=1)

                        
def main():

    img = Image.open(sys.argv[1])
    img_grey = img.convert('L') # convert the image to greyscale
    pix_grey = img_grey.load() # pix[x,y] = (r,g,b)
    draw_grey = ImageDraw.Draw(img_grey)

    """
    for i in xrange(7, width, 8):
        draw_grey.line((i,0,i,height))
    
    for i in xrange(7, height, 8):
        draw_grey.line((0,i,width,i))
    """
    
    getGradient(pix_grey, img.size, draw_grey)
    
    img_grey.show()

    img_grey.save("output.png")

    return 0

if __name__ == "__main__":
    main()    
